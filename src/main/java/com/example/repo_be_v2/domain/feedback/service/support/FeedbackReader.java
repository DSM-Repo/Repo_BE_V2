package com.example.repo_be_v2.domain.feedback.service.support;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import com.example.repo_be_v2.domain.feedback.domain.repository.FeedbackRepository;
import com.example.repo_be_v2.domain.feedback.exception.FeedbackAccessDeniedException;
import com.example.repo_be_v2.domain.feedback.exception.FeedbackPageNotFoundException;
import com.example.repo_be_v2.domain.feedback.exception.FeedbackNotOwnerException;
import com.example.repo_be_v2.domain.feedback.exception.FeedbackNotWriterException;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.exception.ResumeDeletedException;
import com.example.repo_be_v2.domain.resume.exception.ResumeNotFoundException;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.enums.Role;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.exception.TeacherPermissionRequiredException;
import com.example.repo_be_v2.domain.user.exception.UserNotFoundException;
import com.example.repo_be_v2.domain.feedback.exception.FeedbackNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

//피드백 서비스들이 공통으로 쓰는 조회와 검증을 모아둔다.
@Component
@RequiredArgsConstructor
public class FeedbackReader {

    private final FeedbackRepository feedbackRepository;
    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    //MySQL에 실제 사용자가 존재하는지 확인
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    //선생님 권한을 가진 사용자인지 확인
    public User getTeacher(Long userId) {
        User user = getUser(userId);

        if (user.getRole() != Role.TEACHER) {
            throw new TeacherPermissionRequiredException();
        }

        return user;
    }

    public Feedback getFeedback(String feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(FeedbackNotFoundException::new);
    }

    //피드백을 달 문서(이력서)를 조회, 삭제된 이력서는 조회하지 못한다.
    public Resume getResume(String documentId) {
        Resume resume = resumeRepository.findById(documentId)
                .orElseThrow(ResumeNotFoundException::new);

        if (resume.getSubmissionStatus() == ResumeSubmissionStatus.DELETED) {
            throw new ResumeDeletedException();
        }

        return resume;
    }

    //피드백을 볼 수 있는 사용자인지 확인, 선생님은 모든 피드백을 볼 수 있다.
    public void validateReadable(User user, Resume resume) {
        if (user.getRole() == Role.TEACHER) {
            return;
        }

        if (!resume.getUserId().equals(user.getId())) {
            throw new FeedbackAccessDeniedException();
        }
    }

    //본인이 작성한 피드백인지 확인 (선생님의 수정·삭제용)
    public void validateWriter(Long teacherId, Feedback feedback) {
        if (!feedback.getTeacherId().equals(teacherId)) {
            throw new FeedbackNotWriterException();
        }
    }

    //본인 이력서에 달린 피드백인지 확인 (학생의 반영 처리용)
    public void validateOwner(Long studentId, Feedback feedback) {
        Resume resume = resumeRepository.findById(feedback.getResumeId())
                .orElseThrow(ResumeNotFoundException::new);

        if (!resume.getUserId().equals(studentId)) {
            throw new FeedbackNotOwnerException();
        }
    }

    //피드백을 달 페이지가 실제로 그 이력서에 있는지 확인한다. 없으면 404.
    public void validatePage(Resume resume, String pageId) {
        if (resume.getPages() == null) {
            throw new FeedbackPageNotFoundException();
        }

        boolean exists = resume.getPages().stream()
                .anyMatch(page -> page.hasId(pageId));

        if (!exists) {
            throw new FeedbackPageNotFoundException();
        }
    }

    /**
     * 이력서가 지금 들고 있는 페이지들의 id → 순서(index) 맵.
     *
     * 피드백 목록 정렬과 "가리키던 페이지가 사라졌는지" 판정에 함께 쓴다.
     * 맵에 없는 pageId는 이력서에서 지워진 페이지라는 뜻이다.
     *
     * 같은 id를 가진 페이지가 둘 이상 저장돼 있을 수 있어(저장 시 id 중복을 막지 않는다)
     * 먼저 나온 것을 채택한다. merge 함수가 없으면 조회 자체가 실패한다.
     */
    public Map<String, Integer> resolvePageOrders(Resume resume) {
        if (resume.getPages() == null) {
            return Map.of();
        }

        return resume.getPages().stream()
                .filter(page -> page.getId() != null)
                .collect(Collectors.toMap(
                        ResumePage::getId,
                        ResumePage::getIndex,
                        (first, second) -> first
                ));
    }
}
