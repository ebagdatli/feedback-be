package com.feedback.app.exception;

public class AnalysisNotFoundException extends RuntimeException {

    public AnalysisNotFoundException(Long id) {
        super("Analysis not found with id: " + id);
    }

    public AnalysisNotFoundException(String ticketId) {
        super("Analysis not found for ticket: " + ticketId);
    }
}
