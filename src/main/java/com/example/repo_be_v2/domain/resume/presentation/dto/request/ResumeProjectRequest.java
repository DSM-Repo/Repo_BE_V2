package com.example.repo_be_v2.domain.resume.presentation.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * 프로젝트 페이지 머리말.
 *
 * 작성 중에는 비어 있을 수 있어 필수 값이 없다.
 * 이름이 비었는지는 제출할 때 확인한다.
 */
public record ResumeProjectRequest(

        @Size(max = 100, message = "프로젝트 이름은 100자 이하여야 합니다.")
        String name,

        @Size(max = 500, message = "프로젝트 이미지 URL은 500자 이하여야 합니다.")
        @Pattern(regexp = "^https?://.+", message = "유효하지 않은 이미지 URL입니다.")
        String imageUrl,

        @Size(max = 200, message = "프로젝트 소개는 200자 이하여야 합니다.")
        String summary,

        LocalDate startDate,

        LocalDate endDate

) {
}
