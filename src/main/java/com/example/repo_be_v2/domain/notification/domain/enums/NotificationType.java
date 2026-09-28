package com.example.repo_be_v2.domain.notification.domain.enums;

/**
 * 알림 종류.
 *
 * 화면은 이 값을 보고 어느 페이지로 갈지 정한다.
 * 서버가 프론트 경로를 직접 내려주지 않으므로, 라우팅이 바뀌어도 서버는 그대로다.
 */
public enum NotificationType {

    //내 이력서에 선생님이 피드백을 남겼다. resumeId + feedbackId로 이동한다.
    FEEDBACK_CREATED
}
