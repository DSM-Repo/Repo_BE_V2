package com.example.repo_be_v2.domain.notification.presentation.dto.response;

import com.example.repo_be_v2.domain.notification.domain.Notification;
import com.example.repo_be_v2.domain.notification.domain.enums.NotificationType;

import java.time.LocalDateTime;

/**
 * 알림 한 건.
 *
 * 화면은 type을 보고 어디로 갈지 정하고, resumeId·feedbackId로 대상을 짚는다.
 * 해당 없는 종류의 알림에서는 그 값들이 null이다.
 */
public record NotificationResponse(
        String alramId,
        NotificationType type,
        String content,
        String resumeId,
        String feedbackId,
        boolean isRead,
        LocalDateTime createdAt
) {

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getContent(),
                notification.getResumeId(),
                notification.getFeedbackId(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
