package com.example.repo_be_v2.domain.notification.service.support;

import com.example.repo_be_v2.domain.notification.domain.Notification;
import com.example.repo_be_v2.domain.notification.domain.repository.NotificationRepository;
import com.example.repo_be_v2.domain.notification.exception.NotificationAccessDeniedException;
import com.example.repo_be_v2.domain.notification.exception.NotificationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

//알림 서비스들이 공통으로 쓰는 조회와 검증을 모아둔다.
@Component
@RequiredArgsConstructor
public class NotificationReader {

    private final NotificationRepository notificationRepository;

    public List<Notification> getNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 본인 알림만 읽거나 지울 수 있다.
     *
     * 남의 알림을 403으로 돌려주면 그 id가 존재한다는 사실이 새어 나가지만,
     * 알림 id는 추측으로 맞힐 수 있는 값이 아니고 기존 피드백 도메인도 같은 방식이라 맞춘다.
     */
    public Notification getOwnedNotification(Long userId, String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        if (!notification.isOwnedBy(userId)) {
            throw new NotificationAccessDeniedException();
        }

        return notification;
    }
}
