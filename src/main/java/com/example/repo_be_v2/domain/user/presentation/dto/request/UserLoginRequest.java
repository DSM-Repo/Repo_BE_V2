package com.example.repo_be_v2.domain.user.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(

        @NotBlank(message = "이메일을 공백으로 둘 수 없습니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        String studentEmail,

        @NotBlank(message = "비밀번호는 공백으로 둘 순 없습니다.")
        String password
) {
}
