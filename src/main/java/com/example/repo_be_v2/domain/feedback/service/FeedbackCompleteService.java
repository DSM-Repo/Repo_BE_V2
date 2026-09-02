package com.example.repo_be_v2.domain.feedback.service;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.repository.FeedbackRepository;
import com.example.repo_be_v2.domain.feedback.presentation.dto.response.FeedbackStatusResponse;
import com.example.repo_be_v2.domain.feedback.service.support.FeedbackReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedbackCompleteService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackReader feedbackReader;

    /**
     * 피드백 반영 처리 (학생)
     *
     * 본인 이력서에 달린 피드백만 반영 처리할 수 있다.
     */
    @Transactional
    public FeedbackStatusResponse execute(Long studentId, String feedbackId) {
        feedbackReader.getUser(studentId);

        Feedback feedback = feedbackReader.getFeedback(feedbackId);
        feedbackReader.validateOwner(studentId, feedback);

        feedback.complete(LocalDateTime.now());

        return FeedbackStatusResponse.from(feedbackRepository.save(feedback));
    }
}
