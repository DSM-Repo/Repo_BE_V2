package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeStatusSummary;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeStudentListResponse;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeStudentStatusResponse;
import com.example.repo_be_v2.domain.resume.service.support.ResumeReader;
import com.example.repo_be_v2.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ResumeStudentListService {

    //학년도는 3월에 시작한다. 1~2월은 아직 전년도 학년도다.
    private static final int SCHOOL_YEAR_START_MONTH = 3;

    private final ResumeReader resumeReader;

    /**
     * 학생 이력서 제출 현황 조회 (선생님 권한)
     *
     * 학생 정보는 MySQL, 이력서는 MongoDB에 있어 서버에서 합친다.
     * 이력서 쪽이 아니라 학생 쪽을 기준으로 돌기 때문에
     * 아직 이력서를 만들지 않은 학생도 미제출로 목록에 나온다.
     */
    @Transactional(readOnly = true)
    public ResumeStudentListResponse execute(Long teacherId, Integer grade, Integer classNumber) {
        resumeReader.getTeacher(teacherId);

        List<User> students = resumeReader.getStudents(grade, classNumber);

        Map<Long, ResumeStatusSummary> resumes = resumeReader.getResumeStatusByUserId(
                students.stream().map(User::getId).toList()
        );

        List<ResumeStudentStatusResponse> items = students.stream()
                .map(student -> toItem(student, resumes.get(student.getId())))
                .toList();

        return new ResumeStudentListResponse(
                schoolYearOf(LocalDateTime.now()),
                grade,
                classNumber,
                lastUpdatedAtOf(resumes.values()),
                items,
                items.size()
        );
    }

    private ResumeStudentStatusResponse toItem(User student, ResumeStatusSummary resume) {
        ResumeSubmissionStatus status = resume == null ? null : resume.getSubmissionStatus();

        return new ResumeStudentStatusResponse(
                student.getId(),
                student.getStudentName(),
                resumeReader.schoolNumberOf(student),
                student.getStudentGrade(),
                student.getStudentClass(),
                student.getStudentNumber(),
                student.getMajorName(),
                resume == null ? null : resume.getId(),
                status,
                isSubmitted(status),
                resume == null ? null : resume.getSubmittedAt()
        );
    }

    //공개(RELEASED)는 제출한 뒤에만 갈 수 있는 상태라 제출된 것으로 본다. 삭제된 이력서는 미제출이다.
    private boolean isSubmitted(ResumeSubmissionStatus status) {
        return status == ResumeSubmissionStatus.SUBMITTED
                || status == ResumeSubmissionStatus.RELEASED;
    }

    //저장이든 제출이든 가장 최근에 움직인 시각. 이력서가 없으면 null.
    private LocalDateTime lastUpdatedAtOf(Iterable<ResumeStatusSummary> resumes) {
        LocalDateTime latest = null;

        for (ResumeStatusSummary resume : resumes) {
            LocalDateTime candidate = Stream.of(resume.getSavedAt(), resume.getSubmittedAt())
                    .filter(Objects::nonNull)
                    .max(Comparator.naturalOrder())
                    .orElse(null);

            if (candidate != null && (latest == null || candidate.isAfter(latest))) {
                latest = candidate;
            }
        }

        return latest;
    }

    //3월 이전이면 아직 전년도 학년도다. (도서관의 학년도 계산과 같은 규칙)
    private int schoolYearOf(LocalDateTime dateTime) {
        return dateTime.getMonthValue() < SCHOOL_YEAR_START_MONTH
                ? dateTime.getYear() - 1
                : dateTime.getYear();
    }
}
