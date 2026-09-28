package com.example.repo_be_v2.domain.notification.domain.repository;

import com.example.repo_be_v2.domain.notification.domain.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository
        extends MongoRepository<Notification, String> {

    //알림 목록은 최신 것이 위로 온다.
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
