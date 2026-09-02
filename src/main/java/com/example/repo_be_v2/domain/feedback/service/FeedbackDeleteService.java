package com.example.repo_be_v2.domain.feedback.service;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.repository.FeedbackRepository;
import com.example.repo_be_v2.domain.feedback.presentation.dto.response.FeedbackDeleteResponse;
import com.example.repo_be_v2.domain.feedback.service.support.FeedbackReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackDeleteService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackReader feedbackReader;

    /**
     * 피드백 삭제 (선생님 권한)
     *
     * 본인이 작성한 피드백만 삭제할 수 있다.
     */
    @Transactional
    public FeedbackDeleteResponse execute(Long teacherId, String feedbackId) {
        feedbackReader.getTeacher(teacherId);

        Feedback feedback = feedbackReader.getFeedback(feedbackId);
        feedbackReader.validateWriter(teacherId, feedback);

        feedbackRepository.delete(feedback);

        return new FeedbackDeleteResponse("삭제되었습니다.");
    }
}
