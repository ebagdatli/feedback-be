package com.feedback.app.exception;

public class FeedbackNotFoundException extends RuntimeException {

    public FeedbackNotFoundException(Long id) {
        super("Feedback not found with id: " + id);
    }

    public FeedbackNotFoundException(String ticketId) {
        super("Feedback not found with ticketId: " + ticketId);
    }
}
