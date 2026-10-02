package com.example.repo_be_v2.domain.major.presentation.dto.response;

import java.util.List;

/**
 * 전공 하나에 소속된 학생 목록.
 *
 * grade·classNumber는 요청에 쓴 필터를 그대로 돌려준다. 걸지 않았으면 null이다.
 */
public record MajorStudentListResponse(
        Long majorId,
        String majorName,
        Integer grade,
        Integer classNumber,
        List<MajorStudentResponse> students,
        int numberOfData
) {
}
