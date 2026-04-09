package com.feedback.app.repository;

import com.feedback.app.entity.FeedbackAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackAnalysisRepository extends JpaRepository<FeedbackAnalysis, Long> {

    List<FeedbackAnalysis> findByScreenNameAndIssueType(String screenName, String issueType);

    List<FeedbackAnalysis> findByTag(String tag);

    List<FeedbackAnalysis> findBySeverity(String severity);
}
