package com.example.repo_be_v2.domain.feedback.service;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.repository.FeedbackRepository;
import com.example.repo_be_v2.domain.feedback.exception.FeedbackAlreadyExistsException;
import com.example.repo_be_v2.domain.feedback.presentation.dto.request.FeedbackUpdateRequest;
import com.example.repo_be_v2.domain.feedback.presentation.dto.response.FeedbackUpdateResponse;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedbackUpdateService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackReader feedbackReader;
    private final FeedbackSaver feedbackSaver;

    /**
     * 피드백 수정 (선생님 권한)
     *
     * 본인이 작성한 피드백만 수정할 수 있다.
     * 대상 문서는 요청이 아니라 피드백이 들고 있는 resumeId로 정하므로
     * 수정으로 다른 이력서를 가리키게 되는 일은 없다.
     * element_id가 바뀌면 위치를 다시 계산하고, 옮긴 위치에
     * 이미 다른 피드백이 있으면 409를 돌려준다.
     *
     * pageIndex와 updatedAt은 서버가 정하는 값이라 클라이언트가 알 수 없어
     * 수정 결과를 함께 내려준다. 프론트가 재조회하지 않아도 되게 하기 위함이다.
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

        return FeedbackUpdateResponse.from(feedbackSaver.save(feedback), teacher);
    }
}
