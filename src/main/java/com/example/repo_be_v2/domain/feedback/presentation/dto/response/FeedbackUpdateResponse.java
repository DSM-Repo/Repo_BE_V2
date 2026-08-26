package com.example.repo_be_v2.domain.feedback.presentation.dto.response;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.user.domain.User;

import java.time.LocalDateTime;

public record FeedbackUpdateResponse(
        String id,
        String content,
        int pageIndex,
        String teacherName,
        LocalDateTime updatedAt
) {

    public static FeedbackUpdateResponse from(Feedback feedback, User teacher) {
        return new FeedbackUpdateResponse(
                feedback.getId(),
                feedback.getContent(),
                feedback.getPageIndex(),
                teacher == null ? null : teacher.getStudentName(),
                feedback.getUpdatedAt()
        );
    }
}
