package com.example.repo_be_v2.domain.feedback.service;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.presentation.dto.request.FeedbackCreateRequest;
import com.example.repo_be_v2.domain.feedback.presentation.dto.response.FeedbackCreateResponse;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.feedback.service.support.FeedbackReader;
import com.example.repo_be_v2.domain.feedback.service.support.FeedbackSaver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedbackCreateService {

    private final FeedbackReader feedbackReader;
    private final FeedbackSaver feedbackSaver;

    /**
     * 피드백 추가 (선생님 권한)
     *
     * 요청으로 받은 pageId가 그 이력서의 페이지인지 확인한 뒤
     * 페이지 위의 좌표(x, y)와 함께 저장한다.
     * 좌표 기반이라 같은 페이지에 피드백이 여러 개 달릴 수 있다.
     */
    @Transactional
    public FeedbackCreateResponse execute(Long teacherId, FeedbackCreateRequest request) {
        feedbackReader.getTeacher(teacherId);

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

        return FeedbackCreateResponse.from(feedbackSaver.save(feedback));
    }
}
