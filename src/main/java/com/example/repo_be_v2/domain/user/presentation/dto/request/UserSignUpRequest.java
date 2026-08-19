package com.example.repo_be_v2.domain.user.presentation.dto.request;

import com.example.repo_be_v2.domain.user.domain.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record
UserSignUpRequest(
        @NotBlank(message = "이름을 공백으로 둘 수 없습니다.")
        @Size(max = 4, message = "이름은 4자 이하여야 합니다.")
        String studentName,

        @NotBlank(message = "학번을 공백으로 둘 수 없습니다.")
        @Size(max = 30, message = "학번은 30자 이하여야 합니다.")
        String studentId,

        @NotBlank(message = "비밀번호를 공백으로 둘 수 없습니다.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        String password,

        @NotBlank(message = "전공을 공백으로 둘 수 없습니다.")
        String studentMajor,

        @NotNull(message = "권한을 선택해야 합니다.")
        Role role
) {
}
