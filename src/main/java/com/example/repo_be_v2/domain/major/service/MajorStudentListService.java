package com.example.repo_be_v2.domain.major.service;

import com.example.repo_be_v2.domain.major.domain.Major;
import com.example.repo_be_v2.domain.major.presentation.dto.response.MajorStudentListResponse;
import com.example.repo_be_v2.domain.major.presentation.dto.response.MajorStudentResponse;
import com.example.repo_be_v2.domain.major.service.support.MajorReader;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeStatusSummary;
import com.example.repo_be_v2.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MajorStudentListService {

    private final MajorReader majorReader;

    /**
     * 전공별 학생 조회 (선생님 권한)
     *
     * 전공 관리 화면에서 전공을 고르면 그 전공을 쓰는 학생이 나온다.
     * 학생 정보는 MySQL, 이력서는 MongoDB에 있어 서버에서 합치고,
     * 학생 쪽을 기준으로 돌기 때문에 아직 이력서를 만들지 않은 학생도 미제출로 나온다.
     */
    @Transactional(readOnly = true)
    public MajorStudentListResponse execute(
            Long teacherId,
            Long majorId,
            Integer grade,
            Integer classNumber
    ) {
        majorReader.getTeacher(teacherId);

        Major major = majorReader.getMajor(majorId);

        List<User> students = majorReader.getStudentsByMajor(major.getId(), grade, classNumber);

        Map<Long, ResumeStatusSummary> resumes = majorReader.getResumeStatusByUserId(
                students.stream().map(User::getId).toList()
        );

        List<MajorStudentResponse> items = students.stream()
                .map(student -> toItem(student, resumes.get(student.getId())))
                .toList();

        return new MajorStudentListResponse(
                major.getId(),
                major.getName(),
                major.getCreatedAt(),
                grade,
                classNumber,
                items,
                items.size()
        );
    }

    private MajorStudentResponse toItem(User student, ResumeStatusSummary resume) {
        ResumeSubmissionStatus status = resume == null ? null : resume.getSubmissionStatus();

        return new MajorStudentResponse(
                student.getId(),
                student.getStudentName(),
                majorReader.schoolNumberOf(student),
                student.getStudentGrade(),
                student.getStudentClass(),
                student.getStudentNumber(),
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
}
