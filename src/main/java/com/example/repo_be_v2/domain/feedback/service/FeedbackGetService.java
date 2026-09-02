package com.example.repo_be_v2.domain.feedback.service;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.presentation.dto.response.FeedbackResponse;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.feedback.service.support.FeedbackReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackGetService {

    private final FeedbackReader feedbackReader;

    /**
     * 피드백 단건 조회
     *
     * 학생은 본인 이력서에 달린 피드백만, 선생님은 모든 피드백을 볼 수 있다.
     */
    @Transactional(readOnly = true)
    public FeedbackResponse execute(Long userId, String feedbackId) {
        User requester = feedbackReader.getUser(userId);

        Feedback feedback = feedbackReader.getFeedback(feedbackId);

        Resume resume = feedbackReader.getResume(feedback.getResumeId());
        feedbackReader.validateReadable(requester, resume);

        return FeedbackResponse.from(feedback);
    }
}
