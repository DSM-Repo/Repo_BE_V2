package com.example.repo_be_v2.domain.notification.service;

import com.example.repo_be_v2.domain.feedback.domain.event.FeedbackCreatedEvent;
import com.example.repo_be_v2.domain.notification.domain.Notification;
import com.example.repo_be_v2.domain.notification.domain.enums.NotificationType;
import com.example.repo_be_v2.domain.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationCreateService {

    private final NotificationRepository notificationRepository;

    /**
     * 피드백이 달리면 이력서 주인에게 알림을 남긴다.
     *
     * 알림은 피드백의 부수효과다. 알림 저장이 실패해도 피드백 작성까지 실패시키면 안 되므로
     * 여기서 예외를 삼키고 로그만 남긴다. 알림 한 건이 빠지는 것보다 피드백이 사라지는 쪽이 나쁘다.
     */
    @EventListener
    @Transactional
    public void execute(FeedbackCreatedEvent event) {
        try {
            notificationRepository.save(
                    Notification.builder()
                            .userId(event.studentId())
                            .type(NotificationType.FEEDBACK_CREATED)
                            .content("%s 선생님이 이력서에 피드백을 남겼어요.".formatted(event.teacherName()))
                            .resumeId(event.resumeId())
                            .feedbackId(event.feedbackId())
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        } catch (Exception exception) {
            log.error("피드백 알림 저장 실패 (resumeId={}, feedbackId={})",
                    event.resumeId(), event.feedbackId(), exception);
        }
    }
}
