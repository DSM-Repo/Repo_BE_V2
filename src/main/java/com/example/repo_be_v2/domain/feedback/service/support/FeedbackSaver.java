package com.example.repo_be_v2.domain.feedback.service.support;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 피드백 저장을 맡는다. FeedbackReader의 쓰기 쪽 짝이다.
 *
 * 피드백은 페이지 위 좌표에 달리고 같은 페이지에 여러 개가 놓일 수 있어
 * 저장 시 중복으로 막을 자리가 없다.
 */
@Component
@RequiredArgsConstructor
public class FeedbackSaver {

    private final FeedbackRepository feedbackRepository;

    public Feedback save(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }
}
