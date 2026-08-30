package com.example.repo_be_v2.domain.user.presentation.dto.request;

import com.example.repo_be_v2.domain.user.domain.enums.Role;
import jakarta.validation.constraints.*;

public record
UserSignUpRequest(
        @NotBlank(message = "이름을 공백으로 둘 수 없습니다.")
        @Size(max = 4, message = "이름은 4자 이하여야 합니다.")
        String studentName,

        @NotBlank(message = "이메일을 공백으로 둘 수 없습니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@dsm\\.hs\\.kr$",
                message = "dsm.hs.kr 이메일만 사용할 수 있습니다."
        )
        String email,

        @NotNull(message = "학년을 입력해야 합니다.")
        Integer studentGrade,

        @NotNull(message = "반을 입력해야 합니다.")
        Integer studentClass,

        @NotNull(message = "번호를 입력해야 합니다.")
        Integer studentNumber,

        @NotBlank(message = "비밀번호를 공백으로 둘 수 없습니다.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        String password,

        @NotNull(message = "권한을 선택해야 합니다.")
        Role role
) {
}
