package com.example.repo_be_v2.domain.feedback.domain;

import com.example.repo_be_v2.domain.feedback.domain.enums.FeedbackStatus;
import com.example.repo_be_v2.domain.feedback.exception.FeedbackAlreadyCompletedException;
import com.example.repo_be_v2.domain.feedback.exception.FeedbackNotCompletedException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 피드백은 이력서 페이지 위의 좌표에 달린다.
 * 같은 페이지에 여러 개가 달릴 수 있으므로 unique 제약은 없고,
 * 목록 조회가 항상 resumeId(+ pageId)로 들어오므로 그에 맞춘 인덱스만 둔다.
 * resumeId가 접두사라 resumeId 단독 조회도 이 인덱스가 처리한다.
 */
@Document(collection = "feedbacks")
@CompoundIndex(
        name = "idx_feedback_resume_page",
        def = "{'resumeId': 1, 'pageId': 1, 'createdAt': 1}"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback {

    @Id
    private String id;

    //피드백이 달린 이력서(문서)의 MongoDB id
    private String resumeId;

    //피드백이 달린 이력서 페이지의 id
    private String pageId;

    //페이지 좌상단을 원점으로 한 절대 px 좌표
    private double x;

    private double y;

    //피드백을 작성한 선생님의 MySQL PK
    private Long teacherId;

    private String content;

    private FeedbackStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

    /**
     * 생성 시점에 채워야 하는 값만 받는 빌더용 생성자.
     * id, status, updatedAt, completedAt은 빌더로 지정할 수 없고
     * status는 항상 PENDING으로 시작한다.
     */
    @Builder
    private Feedback(
            String resumeId,
            String pageId,
            double x,
            double y,
            Long teacherId,
            String content,
            LocalDateTime createdAt
    ) {
        this.resumeId = resumeId;
        this.pageId = pageId;
        this.x = x;
        this.y = y;
        this.teacherId = teacherId;
        this.content = content;
        this.status = FeedbackStatus.PENDING;
        this.createdAt = createdAt;
    }

    public void update(
            String pageId,
            double x,
            double y,
            String content,
            LocalDateTime updatedAt
    ) {
        this.pageId = pageId;
        this.x = x;
        this.y = y;
        this.content = content;
        this.updatedAt = updatedAt;
    }

    //이 피드백이 해당 이력서에 달린 것인지 판정한다.
    public boolean isOwnedBy(String resumeId) {
        return this.resumeId.equals(resumeId);
    }

    public boolean isCompleted() {
        return status == FeedbackStatus.COMPLETED;
    }

    public void complete(LocalDateTime completedAt) {
        if (status == FeedbackStatus.COMPLETED) {
            throw new FeedbackAlreadyCompletedException();
        }

        this.status = FeedbackStatus.COMPLETED;
        this.completedAt = completedAt;
    }

    public void pending() {
        if (status != FeedbackStatus.COMPLETED) {
            throw new FeedbackNotCompletedException();
        }

        this.status = FeedbackStatus.PENDING;
        this.completedAt = null;
    }
}
