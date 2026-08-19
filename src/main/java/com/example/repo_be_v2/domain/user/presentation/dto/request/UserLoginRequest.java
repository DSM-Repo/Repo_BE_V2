package com.example.repo_be_v2.domain.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(

        @NotBlank(message = "학번을 공백으로 둘 수 없습니다.")
        String studentId,

        @NotBlank(message = "비밀번호는 공백으로 둘 순 없습니다.")
        String password
) {
}
