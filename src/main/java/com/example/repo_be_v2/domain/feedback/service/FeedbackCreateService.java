package com.example.repo_be_v2.domain.feedback.service;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.repository.FeedbackRepository;
import com.example.repo_be_v2.domain.feedback.exception.FeedbackAlreadyExistsException;
import com.example.repo_be_v2.domain.feedback.presentation.dto.request.FeedbackCreateRequest;
import com.example.repo_be_v2.domain.feedback.presentation.dto.response.FeedbackCreateResponse;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedbackCreateService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackReader feedbackReader;

    /**
     * 피드백 추가 (선생님 권한)
     *
     * 이력서 페이지에서 element_id가 들어있는 페이지를 찾아
     * pageIndex를 계산해 함께 저장한다.
     * 같은 문서의 같은 객체에는 피드백을 하나만 달 수 있다.
     */
    @Transactional
    public FeedbackCreateResponse execute(Long teacherId, FeedbackCreateRequest request) {
        feedbackReader.getTeacher(teacherId);

        Resume resume = feedbackReader.getResume(request.documentId());
        int pageIndex = feedbackReader.resolvePageIndex(resume, request.elementId());

        if (feedbackRepository.existsByResumeIdAndElementId(resume.getId(), request.elementId())) {
            throw new FeedbackAlreadyExistsException();
        }

        Feedback feedback = Feedback.create(
                resume.getId(),
                request.elementId(),
                pageIndex,
                teacherId,
                request.comment(),
                LocalDateTime.now()
        );

        Feedback savedFeedback = save(feedback);

        return new FeedbackCreateResponse(
                savedFeedback.getId(),
                savedFeedback.getPageIndex(),
                savedFeedback.getCreatedAt()
        );
    }

    /**
     * 위의 exists 검사와 저장 사이에 다른 요청이 같은 자리를 차지할 수 있어
     * (resumeId, elementId) unique 인덱스 위반을 같은 도메인 예외로 바꿔준다.
     */
    private Feedback save(Feedback feedback) {
        try {
            return feedbackRepository.save(feedback);
        } catch (DuplicateKeyException e) {
            throw new FeedbackAlreadyExistsException();
        }
    }
}
