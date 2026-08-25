package com.example.repo_be_v2.domain.feedback.presentation.dto.response;

import java.time.LocalDateTime;

public record FeedbackCreateResponse(
        String feedbackId,
        int pageIndex,
        LocalDateTime createdAt
) {
}
