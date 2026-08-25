package com.example.repo_be_v2.domain.feedback.service;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.repository.FeedbackRepository;
import com.example.repo_be_v2.domain.feedback.exception.FeedbackApplyLimitExceededException;
import com.example.repo_be_v2.domain.feedback.presentation.dto.request.FeedbackApplyRequest;
import com.example.repo_be_v2.domain.feedback.presentation.dto.response.FeedbackApplyFailureResponse;
import com.example.repo_be_v2.domain.feedback.presentation.dto.response.FeedbackApplyResponse;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.exception.ResumeNotFoundException;
import com.example.repo_be_v2.global.error.exception.REPOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackApplyService {

    //한 번에 반영 처리할 수 있는 최대 건수
    private static final int MAX_APPLY_COUNT = 50;

    private final FeedbackRepository feedbackRepository;
    private final ResumeRepository resumeRepository;
    private final FeedbackReader feedbackReader;

    /**
     * 피드백 일괄 반영 (학생)
     *
     * 체크한 피드백 여러 건을 한 번에 반영(또는 반영 취소) 처리한다.
     * 일부가 실패해도 전체를 롤백하지 않고 건별 실패 사유를 돌려준다.
     */
    @Transactional
    public FeedbackApplyResponse execute(Long studentId, FeedbackApplyRequest request) {
        feedbackReader.getUser(studentId);

        if (request.feedbackIds().size() > MAX_APPLY_COUNT) {
            throw new FeedbackApplyLimitExceededException();
        }

        Resume resume = resumeRepository.findByUserId(studentId)
                .orElseThrow(ResumeNotFoundException::new);

        Map<String, Feedback> feedbacks = feedbackRepository.findAllById(request.feedbackIds())
                .stream()
                .collect(Collectors.toMap(Feedback::getId, Function.identity()));

        LocalDateTime now = LocalDateTime.now();
        List<Feedback> succeeded = new ArrayList<>();
        List<FeedbackApplyFailureResponse> failed = new ArrayList<>();

        for (String feedbackId : request.feedbackIds()) {
            String reason = apply(feedbacks.get(feedbackId), resume, request.applied(), now);

            if (reason == null) {
                succeeded.add(feedbacks.get(feedbackId));
            } else {
                failed.add(new FeedbackApplyFailureResponse(feedbackId, reason));
            }
        }

        feedbackRepository.saveAll(succeeded);

        return new FeedbackApplyResponse(succeeded.size(), failed);
    }

    //한 건을 처리하고, 실패하면 사유를 돌려준다. 성공이면 null.
    private String apply(Feedback feedback, Resume resume, boolean applied, LocalDateTime now) {
        if (feedback == null) {
            return "NOT_FOUND";
        }

        if (!feedback.getResumeId().equals(resume.getId())) {
            return "ACCESS_DENIED";
        }

        try {
            if (applied) {
                feedback.complete(now);
            } else {
                feedback.pending();
            }
        } catch (REPOException e) {
            return applied ? "ALREADY_APPLIED" : "NOT_APPLIED";
        }

        return null;
    }
}
