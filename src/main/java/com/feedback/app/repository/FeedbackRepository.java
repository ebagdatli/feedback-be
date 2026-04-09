package com.feedback.app.repository;

import com.feedback.app.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    Optional<Feedback> findByTicketId(String ticketId);

    List<Feedback> findByIsAnalysisFalse();
}
