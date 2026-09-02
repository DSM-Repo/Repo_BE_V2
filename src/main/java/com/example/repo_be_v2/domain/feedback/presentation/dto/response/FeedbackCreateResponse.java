package com.example.repo_be_v2.domain.feedback.presentation.dto.response;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;

import java.time.LocalDateTime;

public record FeedbackCreateResponse(
        String feedbackId,
        String pageId,
        double x,
        double y,
        LocalDateTime createdAt
) {

    public static FeedbackCreateResponse from(Feedback feedback) {
        return new FeedbackCreateResponse(
                feedback.getId(),
                feedback.getPageId(),
                feedback.getX(),
                feedback.getY(),
                feedback.getCreatedAt()
        );
    }
}
