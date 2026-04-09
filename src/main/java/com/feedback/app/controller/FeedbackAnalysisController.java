package com.feedback.app.controller;

import com.feedback.app.dto.FeedbackAnalysisResponse;
import com.feedback.app.service.FeedbackAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analyses")
@RequiredArgsConstructor
public class FeedbackAnalysisController {

    private final FeedbackAnalysisService analysisService;

    @PostMapping("/trigger")
    public ResponseEntity<List<FeedbackAnalysisResponse>> triggerAnalysis() {
        List<FeedbackAnalysisResponse> results = analysisService.triggerAnalysis();
        return ResponseEntity.status(HttpStatus.CREATED).body(results);
    }

    @GetMapping
    public ResponseEntity<List<FeedbackAnalysisResponse>> getAllAnalyses() {
        return ResponseEntity.ok(analysisService.getAllAnalyses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackAnalysisResponse> getAnalysisById(@PathVariable Long id) {
        return ResponseEntity.ok(analysisService.getAnalysisById(id));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<FeedbackAnalysisResponse>> getByTag(@PathVariable String tag) {
        return ResponseEntity.ok(analysisService.getAnalysesByTag(tag));
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<FeedbackAnalysisResponse>> getBySeverity(@PathVariable String severity) {
        return ResponseEntity.ok(analysisService.getAnalysesBySeverity(severity));
    }

    @GetMapping("/unanalyzed-count")
    public ResponseEntity<Map<String, Long>> getUnanalyzedCount() {
        long count = analysisService.getUnanalyzedCount();
        return ResponseEntity.ok(Map.of("unanalyzedCount", count));
    }
}
