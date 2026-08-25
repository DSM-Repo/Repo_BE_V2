package com.example.repo_be_v2.domain.feedback.service;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.repository.FeedbackRepository;
import com.example.repo_be_v2.domain.feedback.exception.FeedbackAlreadyExistsException;
import com.example.repo_be_v2.domain.feedback.presentation.dto.request.FeedbackUpdateRequest;
import com.example.repo_be_v2.domain.feedback.presentation.dto.response.FeedbackUpdateResponse;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedbackUpdateService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackReader feedbackReader;

    /**
     * 피드백 수정 (선생님 권한)
     *
     * 본인이 작성한 피드백만 수정할 수 있다.
     * 대상 문서는 요청이 아니라 피드백이 들고 있는 resumeId로 정하므로
     * 수정으로 다른 이력서를 가리키게 되는 일은 없다.
     * element_id가 바뀌면 위치를 다시 계산하고, 옮긴 위치에
     * 이미 다른 피드백이 있으면 409를 돌려준다.
     */
    @Transactional
    public FeedbackUpdateResponse execute(
            Long teacherId,
            String feedbackId,
            FeedbackUpdateRequest request
    ) {
        User teacher = feedbackReader.getTeacher(teacherId);

        Feedback feedback = feedbackReader.getFeedback(feedbackId);
        feedbackReader.validateWriter(teacherId, feedback);

        Resume resume = feedbackReader.getResume(feedback.getResumeId());
        int pageIndex = feedbackReader.resolvePageIndex(resume, request.elementId());

        if (!request.elementId().equals(feedback.getElementId())
                && feedbackRepository.existsByResumeIdAndElementId(feedback.getResumeId(), request.elementId())) {
            throw new FeedbackAlreadyExistsException();
        }

        feedback.update(
                request.elementId(),
                pageIndex,
                request.comment(),
                LocalDateTime.now()
        );

        Feedback savedFeedback = save(feedback);

        return new FeedbackUpdateResponse(
                savedFeedback.getId(),
                savedFeedback.getContent(),
                savedFeedback.getPageIndex(),
                teacher.getStudentName(),
                savedFeedback.getUpdatedAt()
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
