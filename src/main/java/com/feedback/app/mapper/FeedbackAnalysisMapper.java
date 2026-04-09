package com.feedback.app.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feedback.app.dto.FeedbackAnalysisResponse;
import com.feedback.app.dto.GeminiAnalysisResult;
import com.feedback.app.entity.FeedbackAnalysis;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Component
public class FeedbackAnalysisMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public FeedbackAnalysisResponse toResponse(FeedbackAnalysis entity) {
        return FeedbackAnalysisResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .screenName(entity.getScreenName())
                .issueType(entity.getIssueType())
                .referenceTicketIds(parseJsonList(entity.getReferenceTicketIds(), new TypeReference<>() {}))
                .referenceFeedbackIds(parseJsonList(entity.getReferenceFeedbackIds(), new TypeReference<>() {}))
                .tag(entity.getTag())
                .severity(entity.getSeverity())
                .status(entity.getStatus())
                .analyzedAt(entity.getAnalyzedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public FeedbackAnalysis toEntity(GeminiAnalysisResult result) {
        return FeedbackAnalysis.builder()
                .title(result.getTitle())
                .description(result.getDescription())
                .screenName(result.getScreenName())
                .issueType(result.getIssueType())
                .referenceTicketIds(toJson(result.getReferenceTicketIds()))
                .referenceFeedbackIds(toJson(result.getReferenceFeedbackIds()))
                .tag(result.getTag())
                .severity(result.getSeverity())
                .status("COMPLETED")
                .analyzedAt(LocalDateTime.now())
                .build();
    }

    private <T> List<T> parseJsonList(String json, TypeReference<List<T>> typeRef) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
