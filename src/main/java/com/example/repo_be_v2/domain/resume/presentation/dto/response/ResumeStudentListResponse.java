package com.example.repo_be_v2.domain.resume.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 선생님의 학생 이력서 제출 현황.
 *
 * schoolYear는 현재 학년도, lastUpdatedAt은 목록 안 이력서 중 가장 최근에 저장·제출된 시각이다.
 * 이력서가 하나도 없으면 lastUpdatedAt은 null이다.
 */
public record ResumeStudentListResponse(
        int schoolYear,
        Integer grade,
        Integer classNumber,
        LocalDateTime lastUpdatedAt,
        List<ResumeStudentStatusResponse> students,
        int numberOfData
) {
}
