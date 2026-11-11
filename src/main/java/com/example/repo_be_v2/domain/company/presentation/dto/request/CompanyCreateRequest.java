package com.example.repo_be_v2.domain.company.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyCreateRequest(

        @NotBlank(message = "회사 이름을 공백으로 둘 수 없습니다.")
        @Size(max = 50, message = "회사 이름은 50자 이하여야 합니다.")
        String name,

        @NotBlank(message = "이메일을 공백으로 둘 수 없습니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        @Size(max = 255, message = "이메일은 255자 이하여야 합니다.")
        String email,

        @NotBlank(message = "비밀번호를 공백으로 둘 수 없습니다.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        String password

) {
}
