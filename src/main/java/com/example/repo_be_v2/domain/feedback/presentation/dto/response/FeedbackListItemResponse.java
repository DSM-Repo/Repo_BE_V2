package com.example.repo_be_v2.domain.feedback.presentation.dto.response;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.enums.FeedbackStatus;
import com.example.repo_be_v2.domain.user.domain.User;

import java.time.LocalDateTime;

public record FeedbackListItemResponse(
        String feedbackId,
        String pageId,
        double x,
        double y,
        String content,
        FeedbackStatus status,
        String teacherName,
        LocalDateTime createdAt,
        LocalDateTime completedAt,

        //가리키던 페이지가 이력서에서 지워졌으면 true. 이때 pageId와 x, y는 그릴 자리가 없다.
        boolean pageDeleted
) {

    //탈퇴 등으로 선생님 정보가 없으면 이름 없이 내려준다.
    public static FeedbackListItemResponse from(Feedback feedback, User teacher, boolean pageDeleted) {
        return new FeedbackListItemResponse(
                feedback.getId(),
                feedback.getPageId(),
                feedback.getX(),
                feedback.getY(),
                feedback.getContent(),
                feedback.getStatus(),
                teacher == null ? null : teacher.getStudentName(),
                feedback.getCreatedAt(),
                feedback.getCompletedAt(),
                pageDeleted
        );
    }
}
