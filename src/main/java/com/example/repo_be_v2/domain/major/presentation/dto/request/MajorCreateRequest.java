package com.example.repo_be_v2.domain.major.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MajorCreateRequest(

        @NotBlank(message = "전공 이름을 공백으로 둘 수 없습니다.")
        @Size(max = 30, message = "전공 이름은 30자 이하여야 합니다.")
        String name

) {
}
