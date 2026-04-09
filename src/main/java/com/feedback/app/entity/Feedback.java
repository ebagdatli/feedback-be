package com.feedback.app.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Feedback extends BaseEntity {

    @Column(nullable = false, unique = true, updatable = false)
    private String ticketId;

    @Column(nullable = false)
    private String issueType;

    @Column(nullable = false)
    private String screenName;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String feedbackText;

    @Column(nullable = false)
    @Builder.Default
    private String status = "NEW";

    @Column(nullable = false)
    @Builder.Default
    private String priority = "MEDIUM";

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAnalysis = false;

    @PrePersist
    public void generateTicketId() {
        if (this.ticketId == null) {
            this.ticketId = "TK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
