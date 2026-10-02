package com.example.repo_be_v2.domain.major.presentation.dto.response;

import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;

import java.time.LocalDateTime;

/**
 * 전공 관리 화면에 보이는 학생 한 명.
 *
 * 이력서를 아직 만들지 않은 학생도 목록에 나와야 하므로
 * resumeId·submissionStatus·submittedAt은 null일 수 있다. 그때 submitted는 false다.
 */
public record MajorStudentResponse(
        Long studentId,
        String name,
        String schoolNumber,
        int grade,
        int classNumber,
        int number,
        String resumeId,
        ResumeSubmissionStatus submissionStatus,
        boolean submitted,
        LocalDateTime submittedAt
) {
}
