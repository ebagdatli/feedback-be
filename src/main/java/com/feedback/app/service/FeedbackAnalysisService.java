package com.feedback.app.service;

import com.feedback.app.dto.FeedbackAnalysisResponse;
import com.feedback.app.dto.GeminiAnalysisResult;
import com.feedback.app.entity.Feedback;
import com.feedback.app.entity.FeedbackAnalysis;
import com.feedback.app.exception.AnalysisNotFoundException;
import com.feedback.app.mapper.FeedbackAnalysisMapper;
import com.feedback.app.repository.FeedbackAnalysisRepository;
import com.feedback.app.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackAnalysisService {

    private static final int MIN_REFERENCE_COUNT = 3;

    private final FeedbackAnalysisRepository analysisRepository;
    private final FeedbackRepository feedbackRepository;
    private final FeedbackAnalysisMapper analysisMapper;
    private final GeminiService geminiService;

    @Transactional
    public List<FeedbackAnalysisResponse> triggerAnalysis() {
        List<Feedback> unanalyzed = feedbackRepository.findByIsAnalysisFalse();

        if (unanalyzed.isEmpty()) {
            log.info("Analiz edilecek feedback bulunamadı.");
            return List.of();
        }

        log.info("{} adet analiz edilmemiş feedback Gemini'ye gönderiliyor.", unanalyzed.size());

        List<GeminiAnalysisResult> results = geminiService.analyzeFeedbacks(unanalyzed);

        List<GeminiAnalysisResult> filtered = results.stream()
                .filter(r -> r.getReferenceTicketIds() != null
                        && r.getReferenceTicketIds().size() >= MIN_REFERENCE_COUNT)
                .toList();

        log.info("Gemini {} sonuç döndü, {} tanesi minimum {} referans kriterini karşılıyor.",
                results.size(), filtered.size(), MIN_REFERENCE_COUNT);

        List<FeedbackAnalysis> savedAnalyses = new ArrayList<>();
        Set<Long> analyzedFeedbackIds = new HashSet<>();

        for (GeminiAnalysisResult result : filtered) {
            FeedbackAnalysis entity = analysisMapper.toEntity(result);
            savedAnalyses.add(analysisRepository.save(entity));

            if (result.getReferenceFeedbackIds() != null) {
                analyzedFeedbackIds.addAll(result.getReferenceFeedbackIds());
            }
        }

        markReferencedFeedbacks(unanalyzed, analyzedFeedbackIds);

        log.info("{} adet analiz sonucu kaydedildi. {} feedback IS_ANALYSIS olarak işaretlendi.",
                savedAnalyses.size(), analyzedFeedbackIds.size());

        return savedAnalyses.stream()
                .map(analysisMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeedbackAnalysisResponse> getAllAnalyses() {
        return analysisRepository.findAll()
                .stream()
                .map(analysisMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FeedbackAnalysisResponse getAnalysisById(Long id) {
        FeedbackAnalysis analysis = analysisRepository.findById(id)
                .orElseThrow(() -> new AnalysisNotFoundException(id));
        return analysisMapper.toResponse(analysis);
    }

    @Transactional(readOnly = true)
    public List<FeedbackAnalysisResponse> getAnalysesByTag(String tag) {
        return analysisRepository.findByTag(tag)
                .stream()
                .map(analysisMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeedbackAnalysisResponse> getAnalysesBySeverity(String severity) {
        return analysisRepository.findBySeverity(severity)
                .stream()
                .map(analysisMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnanalyzedCount() {
        return feedbackRepository.findByIsAnalysisFalse().size();
    }

    private void markReferencedFeedbacks(List<Feedback> allFeedbacks, Set<Long> analyzedFeedbackIds) {
        List<Feedback> toUpdate = allFeedbacks.stream()
                .filter(f -> analyzedFeedbackIds.contains(f.getId()))
                .toList();

        for (Feedback feedback : toUpdate) {
            feedback.setIsAnalysis(true);
            feedback.setStatus("IS_ANALYSIS");
        }
        feedbackRepository.saveAll(toUpdate);
    }
}
