package com.feedback.app.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackUpdateRequest {

    private String status;
    private String priority;
    private Boolean isAnalysis;
}
