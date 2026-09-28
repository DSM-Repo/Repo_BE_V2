package com.example.repo_be_v2.domain.notification.domain;

import com.example.repo_be_v2.domain.notification.domain.enums.NotificationType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 사용자 한 명에게 가는 알림 한 건.
 *
 * 목록은 언제나 받는 사람(userId) 기준으로 최신순 조회라 그에 맞춘 인덱스만 둔다.
 * 이동에 필요한 값(resumeId, feedbackId)은 알림이 직접 들고 있어서
 * 화면이 type만 보고 바로 이동할 수 있다.
 */
@Document(collection = "notifications")
@CompoundIndex(
        name = "idx_notification_user_created",
        def = "{'userId': 1, 'createdAt': -1}"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    private String id;

    //알림을 받는 사용자의 MySQL PK
    private Long userId;

    private NotificationType type;

    private String content;

    //이동할 이력서. 피드백 알림은 항상 값이 있다.
    private String resumeId;

    //이동할 피드백. 이력서 안에서 어느 피드백인지 짚어준다.
    private String feedbackId;

    private boolean isRead;

    private LocalDateTime createdAt;

    /**
     * 생성 시점에 채워야 하는 값만 받는 빌더용 생성자.
     * id와 isRead는 빌더로 지정할 수 없고, 알림은 항상 읽지 않은 상태로 시작한다.
     */
    @Builder
    private Notification(
            Long userId,
            NotificationType type,
            String content,
            String resumeId,
            String feedbackId,
            LocalDateTime createdAt
    ) {
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.resumeId = resumeId;
        this.feedbackId = feedbackId;
        this.isRead = false;
        this.createdAt = createdAt;
    }

    //이 알림이 해당 사용자의 것인지 판정한다.
    public boolean isOwnedBy(Long userId) {
        return this.userId.equals(userId);
    }

    //이미 읽은 알림을 다시 눌러도 그대로 둔다. 읽음은 되돌릴 일이 없다.
    public void read() {
        this.isRead = true;
    }
}
