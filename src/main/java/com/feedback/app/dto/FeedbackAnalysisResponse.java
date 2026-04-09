package com.feedback.app.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackAnalysisResponse {

    private Long id;
    private String title;
    private String description;
    private String screenName;
    private String issueType;
    private List<String> referenceTicketIds;
    private List<Long> referenceFeedbackIds;
    private String tag;
    private String severity;
    private String status;
    private LocalDateTime analyzedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
