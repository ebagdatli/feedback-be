package com.feedback.app.service;

import com.feedback.app.dto.FeedbackRequest;
import com.feedback.app.dto.FeedbackResponse;
import com.feedback.app.dto.FeedbackUpdateRequest;
import com.feedback.app.entity.Feedback;
import com.feedback.app.exception.FeedbackNotFoundException;
import com.feedback.app.mapper.FeedbackMapper;
import com.feedback.app.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;

    @Transactional
    public FeedbackResponse createFeedback(FeedbackRequest request) {
        Feedback feedback = feedbackMapper.toEntity(request);
        Feedback saved = feedbackRepository.save(feedback);
        return feedbackMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getAllFeedbacks() {
        return feedbackRepository.findAll()
                .stream()
                .map(feedbackMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public FeedbackResponse getFeedbackById(Long id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new FeedbackNotFoundException(id));
        return feedbackMapper.toResponse(feedback);
    }

    @Transactional
    public FeedbackResponse updateFeedback(Long id, FeedbackUpdateRequest request) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new FeedbackNotFoundException(id));

        feedbackMapper.updateEntityFromDto(request, feedback);
        Feedback updated = feedbackRepository.save(feedback);
        return feedbackMapper.toResponse(updated);
    }

    @Transactional
    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) {
            throw new FeedbackNotFoundException(id);
        }
        feedbackRepository.deleteById(id);
    }
}
