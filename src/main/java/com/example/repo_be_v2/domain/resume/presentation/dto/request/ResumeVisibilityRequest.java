package com.example.repo_be_v2.domain.resume.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record ResumeVisibilityRequest(

        @NotNull(message = "공개 여부가 필요합니다.")
        Boolean isPublic

) {
}