package com.example.repo_be_v2.domain.feedback.service;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.repository.FeedbackRepository;
import com.example.repo_be_v2.domain.feedback.exception.FeedbackAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

/**
 * 피드백 저장을 맡는다. FeedbackReader의 쓰기 쪽 짝이다.
 *
 * 서비스의 exists 검사와 저장 사이에 다른 요청이 같은 자리를 차지할 수 있어
 * (resumeId, elementId) unique 인덱스 위반을 같은 도메인 예외로 바꿔준다.
 * DuplicateKeyException은 Spring Data의 인프라 예외라 도메인 엔티티가 아니라
 * 이 계층에서 번역한다.
 */
@Component
@RequiredArgsConstructor
public class FeedbackSaver {

    private final FeedbackRepository feedbackRepository;

    public Feedback save(Feedback feedback) {
        try {
            return feedbackRepository.save(feedback);
        } catch (DuplicateKeyException e) {
            throw new FeedbackAlreadyExistsException();
        }
    }
}
