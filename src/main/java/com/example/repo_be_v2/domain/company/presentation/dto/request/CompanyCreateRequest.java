package com.example.repo_be_v2.domain.company.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

//인증 코드는 서버가 만들어 주므로 선생님은 회사 이름만 넣는다.
public record CompanyCreateRequest(

        @NotBlank(message = "회사 이름을 공백으로 둘 수 없습니다.")
        @Size(max = 50, message = "회사 이름은 50자 이하여야 합니다.")
        String name

) {
}
