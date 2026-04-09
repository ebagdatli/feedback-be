package com.feedback.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackRequest {

    @NotBlank(message = "Issue type is required")
    private String issueType;

    @NotBlank(message = "Screen name is required")
    private String screenName;

    @NotBlank(message = "URL is required")
    private String url;

    @NotBlank(message = "Feedback text is required")
    private String feedbackText;

    @NotBlank(message = "Created by is required")
    private String createdBy;
}
