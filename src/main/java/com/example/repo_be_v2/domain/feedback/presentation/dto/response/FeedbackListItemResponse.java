package com.example.repo_be_v2.domain.feedback.presentation.dto.response;

import com.example.repo_be_v2.domain.feedback.domain.enums.FeedbackStatus;

import java.time.LocalDateTime;

public record FeedbackListItemResponse(
        String feedbackId,
        String elementId,
        int pageIndex,
        String content,
        FeedbackStatus status,
        String teacherName,
        LocalDateTime createdAt,
        LocalDateTime completedAt
) {
}
