package com.example.repo_be_v2.domain.feedback.domain;

import com.example.repo_be_v2.domain.feedback.domain.enums.FeedbackStatus;
import com.example.repo_be_v2.domain.feedback.exception.FeedbackAlreadyCompletedException;
import com.example.repo_be_v2.domain.feedback.exception.FeedbackNotCompletedException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "feedbacks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback {

    @Id
    private String id;

    //피드백이 달린 이력서(문서)의 MongoDB id
    @Indexed
    private String resumeId;

    //이력서 페이지 content 안에서 피드백이 달린 객체의 id
    private String elementId;

    //elementId가 속한 페이지의 index, 저장 시점에 계산해 둔다
    private int pageIndex;

    //피드백을 작성한 선생님의 MySQL PK
    private Long teacherId;

    private String content;

    private FeedbackStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

    public static Feedback create(
            String resumeId,
            String elementId,
            int pageIndex,
            Long teacherId,
            String content,
            LocalDateTime createdAt
    ) {
        Feedback feedback = new Feedback();

        feedback.resumeId = resumeId;
        feedback.elementId = elementId;
        feedback.pageIndex = pageIndex;
        feedback.teacherId = teacherId;
        feedback.content = content;
        feedback.status = FeedbackStatus.PENDING;
        feedback.createdAt = createdAt;

        return feedback;
    }

    public void update(
            String elementId,
            int pageIndex,
            String content,
            LocalDateTime updatedAt
    ) {
        this.elementId = elementId;
        this.pageIndex = pageIndex;
        this.content = content;
        this.updatedAt = updatedAt;
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
