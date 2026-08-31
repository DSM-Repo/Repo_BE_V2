package com.example.repo_be_v2.domain.feedback.service;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
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

    private final FeedbackReader feedbackReader;
    private final FeedbackSaver feedbackSaver;

    /**
     * 피드백 수정 (선생님 권한)
     *
     * 본인이 작성한 피드백만 수정할 수 있다.
     * 대상 문서는 요청이 아니라 피드백이 들고 있는 resumeId로 정하므로
     * 수정으로 다른 이력서를 가리키게 되는 일은 없다.
     * 페이지를 옮기는 것도 허용하되, 옮길 페이지가 같은 이력서 안에 있어야 한다.
     *
     * updatedAt은 서버가 정하는 값이라 클라이언트가 알 수 없어
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
        feedbackReader.validatePage(resume, request.pageId());

        feedback.update(
                request.pageId(),
                request.x(),
                request.y(),
                request.comment(),
                LocalDateTime.now()
        );

        return FeedbackUpdateResponse.from(feedbackSaver.save(feedback), teacher);
    }
}
