package com.feedback.app.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponse {

    private Long id;
    private String ticketId;
    private String issueType;
    private String screenName;
    private String url;
    private String feedbackText;
    private String status;
    private String priority;
    private Boolean isAnalysis;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
