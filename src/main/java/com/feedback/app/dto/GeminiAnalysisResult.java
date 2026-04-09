package com.feedback.app.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeminiAnalysisResult {

    private String title;
    private String description;
    private String screenName;
    private String issueType;
    private List<String> referenceTicketIds;
    private List<Long> referenceFeedbackIds;
    private String tag;
    private String severity;
}
