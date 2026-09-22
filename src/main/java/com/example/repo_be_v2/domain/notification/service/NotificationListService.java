package com.example.repo_be_v2.domain.notification.service;

import com.example.repo_be_v2.domain.notification.presentation.dto.response.NotificationResponse;
import com.example.repo_be_v2.domain.notification.service.support.NotificationReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationListService {

    private final NotificationReader notificationReader;

    /**
     * 알림 목록 조회
     *
     * 읽은 알림도 같이 내려준다. 화면이 읽음 여부로 표시를 달리하고,
     * 지우기 전까지는 다시 눌러 이동할 수 있어야 하기 때문이다.
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> execute(Long userId) {
        return notificationReader.getNotifications(userId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }
}
