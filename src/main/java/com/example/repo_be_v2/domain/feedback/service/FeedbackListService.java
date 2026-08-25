package com.example.repo_be_v2.domain.feedback.service;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.repository.FeedbackRepository;
import com.example.repo_be_v2.domain.feedback.presentation.dto.response.FeedbackListItemResponse;
import com.example.repo_be_v2.domain.feedback.presentation.dto.response.FeedbackListResponse;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedbackListService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final FeedbackReader feedbackReader;

    /**
     * 문서별 피드백 목록 조회
     *
     * 학생은 본인 이력서의 피드백만, 선생님은 모든 이력서의 피드백을 볼 수 있다.
     * pageIndex를 주면 해당 페이지의 피드백만 내려준다.
     * 정렬은 pageIndex 오름차순, 같은 페이지 안에서는 생성 시각 오름차순.
     */
    @Transactional(readOnly = true)
    public FeedbackListResponse execute(Long userId, String documentId, Integer pageIndex) {
        User requester = feedbackReader.getUser(userId);

        Resume resume = feedbackReader.getResume(documentId);
        feedbackReader.validateReadable(requester, resume);

        List<Feedback> feedbacks = findFeedbacks(resume.getId(), pageIndex);
        Map<Long, User> teachers = findTeachers(feedbacks);

        List<FeedbackListItemResponse> responses = feedbacks.stream()
                .map(feedback -> toItemResponse(feedback, teachers.get(feedback.getTeacherId())))
                .toList();

        return new FeedbackListResponse(responses, responses.size());
    }

    private List<Feedback> findFeedbacks(String resumeId, Integer pageIndex) {
        if (pageIndex == null) {
            return feedbackRepository.findAllByResumeIdOrderByPageIndexAscCreatedAtAsc(resumeId);
        }

        return feedbackRepository.findAllByResumeIdAndPageIndexOrderByCreatedAtAsc(resumeId, pageIndex);
    }

    //피드백 작성자들의 정보를 MySQL에서 한 번에 조회한다.
    private Map<Long, User> findTeachers(List<Feedback> feedbacks) {
        Set<Long> teacherIds = feedbacks.stream()
                .map(Feedback::getTeacherId)
                .collect(Collectors.toSet());

        if (teacherIds.isEmpty()) {
            return Map.of();
        }

        return userRepository.findAllById(teacherIds)
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    //탈퇴 등으로 선생님 정보가 없으면 이름 없이 내려준다.
    private FeedbackListItemResponse toItemResponse(Feedback feedback, User teacher) {
        return new FeedbackListItemResponse(
                feedback.getId(),
                feedback.getElementId(),
                feedback.getPageIndex(),
                feedback.getContent(),
                feedback.getStatus(),
                teacher == null ? null : teacher.getStudentName(),
                feedback.getCreatedAt(),
                feedback.getCompletedAt()
        );
    }
}
