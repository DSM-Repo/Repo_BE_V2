package com.example.repo_be_v2.domain.feedback.presentation.dto.response;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.enums.FeedbackStatus;

import java.time.LocalDateTime;

public record FeedbackResponse(
        String feedbackId,
        String pageId,
        double x,
        double y,
        String content,
        FeedbackStatus status,
        LocalDateTime createdAt
) {

    public static FeedbackResponse from(Feedback feedback) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getPageId(),
                feedback.getX(),
                feedback.getY(),
                feedback.getContent(),
                feedback.getStatus(),
                feedback.getCreatedAt()
        );
    }
}
