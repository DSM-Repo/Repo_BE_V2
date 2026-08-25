package com.example.repo_be_v2.domain.feedback.presentation.dto.response;

import com.example.repo_be_v2.domain.feedback.domain.enums.FeedbackStatus;

import java.time.LocalDateTime;

public record FeedbackResponse(
        String feedbackId,
        String content,
        FeedbackStatus status,
        LocalDateTime createdAt
) {
}
