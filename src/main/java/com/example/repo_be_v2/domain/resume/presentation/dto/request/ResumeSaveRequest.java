package com.example.repo_be_v2.domain.resume.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 이력서 저장 요청. 수동 저장과 자동 저장이 같은 형식을 쓴다.
 *
 * 첫 장 머리말 중 이름, 전공은 여기 없다.
 * 그 둘은 사용자 정보(MySQL)라 PATCH /user로 따로 고친다.
 *
 * 프로필 사진은 POST /image로 올려 받은 URL을 담는다.
 * 저장은 통째로 덮어쓰므로 사진을 바꾸지 않을 때도 조회한 값을 그대로 보내야 한다.
 */
public record ResumeSaveRequest(

        String introduce,

        @Email(message = "유효하지 않은 이메일 형식입니다.")
        @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
        String email,

        List<@Size(max = 30, message = "기술스택은 30자 이하여야 합니다.") String> skills,

        String portfolioUrl,

        @Size(max = 500, message = "프로필 이미지 URL은 500자 이하여야 합니다.")
        @Pattern(regexp = "^https?://.+", message = "유효하지 않은 이미지 URL입니다.")
        String profileImageUrl,

        @NotNull @Valid List<ResumePageRequest> pages

) {
}
