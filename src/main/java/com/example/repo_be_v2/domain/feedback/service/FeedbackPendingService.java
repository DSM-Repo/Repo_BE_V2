package com.example.repo_be_v2.domain.feedback.service;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.repository.FeedbackRepository;
import com.example.repo_be_v2.domain.feedback.presentation.dto.response.FeedbackStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackPendingService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackReader feedbackReader;

    /**
     * 피드백 반영 취소 (학생)
     *
     * 반영 처리한 피드백을 다시 미반영(PENDING) 상태로 되돌린다.
     */
    @Transactional
    public FeedbackStatusResponse execute(Long studentId, String feedbackId) {
        feedbackReader.getUser(studentId);

        Feedback feedback = feedbackReader.getFeedback(feedbackId);
        feedbackReader.validateOwner(studentId, feedback);

        feedback.pending();

        return FeedbackStatusResponse.from(feedbackRepository.save(feedback));
    }
}
