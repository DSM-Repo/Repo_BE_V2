package com.example.repo_be_v2.domain.notification.service;

import com.example.repo_be_v2.domain.notification.domain.Notification;
import com.example.repo_be_v2.domain.notification.domain.repository.NotificationRepository;
import com.example.repo_be_v2.domain.notification.service.support.NotificationReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationDeleteService {

    private final NotificationRepository notificationRepository;
    private final NotificationReader notificationReader;

    //알림 삭제. 본인 알림만 지울 수 있다.
    public void execute(Long userId, String notificationId) {
        Notification notification = notificationReader.getOwnedNotification(userId, notificationId);

        notificationRepository.delete(notification);
    }
}
