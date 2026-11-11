package com.example.repo_be_v2.domain.user.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//기업 계정은 학교 이메일이 아니므로 도메인은 여기서 가리지 않는다. 학교 이메일 제한은 회원가입에만 있다.
public record UserLoginRequest(

        @NotBlank(message = "이메일을 공백으로 둘 수 없습니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        String email,

        @NotBlank(message = "비밀번호는 공백으로 둘 순 없습니다.")
        String password
) {
}
