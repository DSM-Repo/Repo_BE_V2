package com.example.repo_be_v2.domain.feedback.presentation.dto.response;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.enums.FeedbackStatus;
import com.example.repo_be_v2.domain.user.domain.User;

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

    //탈퇴 등으로 선생님 정보가 없으면 이름 없이 내려준다.
    public static FeedbackListItemResponse from(Feedback feedback, User teacher) {
        return new FeedbackListItemResponse(
                feedback.getId(),
                feedback.getElementId(),
                feedback.getPageIndex(),
                feedback.getContent(),
                feedback.getStatus(),
                teacher == null ? null : teacher.getStudentName(),
                feedback.getCreatedAt(),
                feedback.getCompletedAt()
        );
    }
}
