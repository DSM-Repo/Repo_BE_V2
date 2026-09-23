package com.example.repo_be_v2.domain.feedback.service;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.event.FeedbackCreatedEvent;
import com.example.repo_be_v2.domain.feedback.presentation.dto.request.FeedbackCreateRequest;
import com.example.repo_be_v2.domain.feedback.presentation.dto.response.FeedbackCreateResponse;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.feedback.service.support.FeedbackReader;
import com.example.repo_be_v2.domain.feedback.service.support.FeedbackSaver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedbackCreateService {

    private final FeedbackReader feedbackReader;
    private final FeedbackSaver feedbackSaver;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 피드백 추가 (선생님 권한)
     *
     * 요청으로 받은 pageId가 그 이력서의 페이지인지 확인한 뒤
     * 페이지 위의 좌표(x, y)와 함께 저장한다.
     * 좌표 기반이라 같은 페이지에 피드백이 여러 개 달릴 수 있다.
     *
     * 저장한 뒤 이력서 주인에게 알림이 가도록 이벤트를 발행한다.
     * 알림 저장은 받는 쪽에서 실패를 삼키므로 피드백 작성을 막지 않는다.
     */
    @Transactional
    public FeedbackCreateResponse execute(Long teacherId, FeedbackCreateRequest request) {
        User teacher = feedbackReader.getTeacher(teacherId);

        Resume resume = feedbackReader.getResume(request.documentId());
        feedbackReader.validatePage(resume, request.pageId());

        Feedback feedback = Feedback.builder()
                .resumeId(resume.getId())
                .pageId(request.pageId())
                .x(request.x())
                .y(request.y())
                .teacherId(teacherId)
                .content(request.comment())
                .createdAt(LocalDateTime.now())
                .build();

        Feedback savedFeedback = feedbackSaver.save(feedback);

        eventPublisher.publishEvent(new FeedbackCreatedEvent(
                resume.getUserId(),
                teacher.getStudentName(),
                resume.getId(),
                savedFeedback.getId()
        ));

        return FeedbackCreateResponse.from(savedFeedback);
    }
}
