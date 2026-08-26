package com.example.repo_be_v2.domain.feedback.presentation.dto.response;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.enums.FeedbackStatus;

public record FeedbackStatusResponse(
        String feedbackId,
        FeedbackStatus status
) {

    public static FeedbackStatusResponse from(Feedback feedback) {
        return new FeedbackStatusResponse(
                feedback.getId(),
                feedback.getStatus()
        );
    }
}
