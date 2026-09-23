package com.example.repo_be_v2.domain.notification.service;

import com.example.repo_be_v2.domain.notification.domain.Notification;
import com.example.repo_be_v2.domain.notification.domain.repository.NotificationRepository;
import com.example.repo_be_v2.domain.notification.presentation.dto.response.NotificationReadResponse;
import com.example.repo_be_v2.domain.notification.service.support.NotificationReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationReadService {

    private final NotificationRepository notificationRepository;
    private final NotificationReader notificationReader;

    /**
     * 알림 읽음 처리
     *
     * 화면에서 알림을 눌러 이동할 때 같이 호출한다.
     * 이미 읽은 알림을 다시 눌러도 그대로 성공시킨다.
     */
    @Transactional
    public NotificationReadResponse execute(Long userId, String notificationId) {
        Notification notification = notificationReader.getOwnedNotification(userId, notificationId);

        notification.read();

        return new NotificationReadResponse(
                notificationRepository.save(notification).isRead()
        );
    }
}
