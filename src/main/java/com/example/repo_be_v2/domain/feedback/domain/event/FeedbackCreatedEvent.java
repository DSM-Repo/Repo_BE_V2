package com.example.repo_be_v2.domain.feedback.domain.event;

/**
 * 선생님이 피드백을 남겼을 때 발행한다.
 *
 * 알림은 피드백의 부수효과라 피드백 서비스가 알림 코드를 직접 부르지 않는다.
 * 이벤트로 끊어두면 나중에 받는 쪽이 늘어도 피드백 서비스는 그대로다.
 */
public record FeedbackCreatedEvent(
        Long studentId,
        String teacherName,
        String resumeId,
        String feedbackId
) {
}
