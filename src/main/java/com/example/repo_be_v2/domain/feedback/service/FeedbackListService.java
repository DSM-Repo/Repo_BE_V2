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

import java.util.Comparator;
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
     * pageId를 주면 해당 페이지의 피드백만 내려준다.
     * 정렬은 페이지 순서 오름차순, 같은 페이지 안에서는 생성 시각 오름차순.
     * 페이지 순서는 이력서가 들고 있는 값이라 조회 시점에 계산한다.
     */
    @Transactional(readOnly = true)
    public FeedbackListResponse execute(Long userId, String documentId, String pageId) {
        User requester = feedbackReader.getUser(userId);

        Resume resume = feedbackReader.getResume(documentId);
        feedbackReader.validateReadable(requester, resume);

        List<Feedback> feedbacks = sortByPageOrder(resume, findFeedbacks(resume.getId(), pageId));
        Map<Long, User> teachers = findTeachers(feedbacks);

        List<FeedbackListItemResponse> responses = feedbacks.stream()
                .map(feedback -> FeedbackListItemResponse.from(feedback, teachers.get(feedback.getTeacherId())))
                .toList();

        return new FeedbackListResponse(responses, responses.size());
    }

    private List<Feedback> findFeedbacks(String resumeId, String pageId) {
        if (pageId == null || pageId.isBlank()) {
            return feedbackRepository.findAllByResumeIdOrderByCreatedAtAsc(resumeId);
        }

        return feedbackRepository.findAllByResumeIdAndPageIdOrderByCreatedAtAsc(resumeId, pageId);
    }

    //DB는 생성 시각 순으로만 내려주므로 페이지 순서는 여기서 맞춘다. 같은 페이지 안의 순서는 유지된다.
    private List<Feedback> sortByPageOrder(Resume resume, List<Feedback> feedbacks) {
        return feedbacks.stream()
                .sorted(Comparator.comparingInt(
                        feedback -> feedbackReader.resolvePageOrder(resume, feedback.getPageId())
                ))
                .toList();
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
}
