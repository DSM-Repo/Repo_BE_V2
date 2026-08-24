package com.example.repo_be_v2.domain.user.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailVerificationSendRequest(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@dsm\\.hs\\.kr$",
                message = "dsm.hs.kr 이메일만 사용할 수 있습니다."
        )
        String email
) {
}
