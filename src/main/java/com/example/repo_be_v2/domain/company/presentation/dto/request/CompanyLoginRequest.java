package com.example.repo_be_v2.domain.company.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyLoginRequest(

        //구분선이나 공백을 섞어 보내도 받는다. 모양이 맞는지는 서비스에서 가린다.
        @NotBlank(message = "인증 코드를 입력해야 합니다.")
        @Size(max = 64, message = "인증 코드가 너무 깁니다.")
        String code

) {
}
