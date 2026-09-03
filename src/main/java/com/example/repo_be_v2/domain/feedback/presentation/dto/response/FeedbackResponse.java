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
        LocalDateTime createdAt,

        //가리키던 페이지가 이력서에서 지워졌으면 true. 이때 pageId와 x, y는 그릴 자리가 없다.
        boolean pageDeleted
) {

    public static FeedbackResponse from(Feedback feedback, boolean pageDeleted) {
        return new FeedbackResponse(
                feedback.getId(),
                feedback.getPageId(),
                feedback.getX(),
                feedback.getY(),
                feedback.getContent(),
                feedback.getStatus(),
                feedback.getCreatedAt(),
                pageDeleted
        );
    }
}
