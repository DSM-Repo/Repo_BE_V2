package com.example.repo_be_v2.domain.resume.presentation.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 프로젝트 페이지 머리말.
 *
 * 작성 중에는 비어 있을 수 있어 필수 값이 없다.
 * 이름이 비었는지는 제출할 때 확인한다.
 *
 * 기간은 "2026.03", "진행 중"처럼 자유롭게 쓸 수 있어 형식을 검사하지 않고 길이만 막는다.
 */
public record ResumeProjectRequest(

        @Size(max = 100, message = "프로젝트 이름은 100자 이하여야 합니다.")
        String name,

        //업로드·조회로 받은 서명 URL을 그대로 보내도 통과해야 한다. 임시 자격 증명을 쓰면 1KB를 넘기도 한다.
        @Size(max = 2048, message = "프로젝트 이미지 URL은 2048자 이하여야 합니다.")
        @Pattern(regexp = "^https?://.+", message = "유효하지 않은 이미지 URL입니다.")
        String imageUrl,

        @Size(max = 200, message = "프로젝트 소개는 200자 이하여야 합니다.")
        String summary,

        @Size(max = 30, message = "프로젝트 시작일은 30자 이하여야 합니다.")
        String startDate,

        @Size(max = 30, message = "프로젝트 종료일은 30자 이하여야 합니다.")
        String endDate

) {
}
