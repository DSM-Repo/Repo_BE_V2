package com.example.repo_be_v2.domain.library.service.support;

import com.example.repo_be_v2.domain.library.exception.LibraryResumeNotFoundException;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.repository.PublicResumeSummary;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 도서관 서비스들이 공통으로 쓰는 조회와 학년도 계산을 모아둔다.
 *
 * 도서관은 저장하는 데이터가 없다. 공개된 이력서(resumes)와 사용자(tbl_user)를
 * 읽어서 학년도·기수·학년을 그때그때 계산한다.
 */
@Component
@RequiredArgsConstructor
public class LibraryReader {

    /**
     * 기수 = 학년도 - 학년 - 이 상수.
     * 2026학년도 2학년이 11기인 것에 맞춰 정했다.
     */
    private static final int COHORT_BASE_YEAR = 2013;

    //학년도는 3월에 시작한다. 1~2월은 아직 전년도 학년도다.
    private static final int SCHOOL_YEAR_START_MONTH = 3;

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    //도서관에서는 공개된 이력서만 보인다. 비공개나 미제출은 없는 것으로 취급한다.
    public Resume getPublicResume(Long studentId) {
        return resumeRepository.findByUserIdAndIsPublicTrue(studentId)
                .orElseThrow(LibraryResumeNotFoundException::new);
    }

    /**
     * 공개된 이력서 요약(userId, releasedAt)을 모은다. schoolYear가 있으면 그 학년도에 공개된 것만 고른다.
     *
     * 학년도는 저장된 값이 아니라 releasedAt에서 계산하는 값이라 연도로 직접 거를 수 없다.
     * 그래서 해당 학년도의 시작(3월 1일)부터 다음 학년도 시작 직전까지로 범위를 잡는다.
     */
    public List<PublicResumeSummary> getPublicResumes(Integer schoolYear) {
        if (schoolYear == null) {
            return resumeRepository.findByIsPublicTrue();
        }

        return resumeRepository.findPublicReleasedIn(
                schoolYearStart(schoolYear),
                schoolYearStart(schoolYear + 1)
        );
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(LibraryResumeNotFoundException::new);
    }

    public Map<Long, User> getUsersById(Collection<Long> userIds) {
        return userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }

    //3월 이전이면 아직 전년도 학년도다.
    public int schoolYearOf(LocalDateTime dateTime) {
        return dateTime.getMonthValue() < SCHOOL_YEAR_START_MONTH
                ? dateTime.getYear() - 1
                : dateTime.getYear();
    }

    /**
     * 기수는 재학 중에는 변하지 않는다.
     * 진급하면 학년도와 학년이 함께 1씩 오르면서 서로 상쇄되기 때문이다.
     * 그래서 공개 시점에 따로 저장해두지 않고 현재 학년으로 계산한다.
     */
    public int cohortOf(User user) {
        return schoolYearOf(LocalDateTime.now())
                - user.getStudentGrade()
                - COHORT_BASE_YEAR;
    }

    //공개 당시 학년도 저장하지 않는다. 학년도와 기수가 있으면 역산된다.
    public int gradeOf(int schoolYear, int cohort) {
        return schoolYear - cohort - COHORT_BASE_YEAR;
    }

    /**
     * 학번은 학년·반·번호를 이어 붙인다. (2학년 2반 11번 -> "2211")
     *
     * 학년은 응답의 다른 값과 어긋나지 않도록 공개 당시 학년을 쓴다.
     * 반·번호는 저장된 스냅샷이 없어 현재 값을 그대로 쓴다.
     */
    public String studentNumberOf(int grade, User user) {
        return "%d%d%02d".formatted(
                grade,
                user.getStudentClass(),
                user.getStudentNumber()
        );
    }

    private LocalDateTime schoolYearStart(int schoolYear) {
        return LocalDateTime.of(schoolYear, SCHOOL_YEAR_START_MONTH, 1, 0, 0);
    }
}
