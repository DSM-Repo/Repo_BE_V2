package com.example.repo_be_v2.domain.feedback.presentation.dto.response;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;

import java.time.LocalDateTime;

public record FeedbackCreateResponse(
        String feedbackId,
        int pageIndex,
        LocalDateTime createdAt
) {

    public static FeedbackCreateResponse from(Feedback feedback) {
        return new FeedbackCreateResponse(
                feedback.getId(),
                feedback.getPageIndex(),
                feedback.getCreatedAt()
        );
    }
}
