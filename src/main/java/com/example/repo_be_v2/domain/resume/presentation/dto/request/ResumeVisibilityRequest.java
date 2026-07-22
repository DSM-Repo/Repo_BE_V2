package com.example.repo_be_v2.domain.resume.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record ResumeVisibilityRequest(

        @NotNull
        Boolean isPublic

) {
}