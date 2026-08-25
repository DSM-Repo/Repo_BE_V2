package com.example.repo_be_v2.domain.feedback.presentation.dto.response;

import java.time.LocalDateTime;

public record FeedbackUpdateResponse(
        String id,
        String content,
        int pageIndex,
        String teacherName,
        LocalDateTime updatedAt
) {
}
