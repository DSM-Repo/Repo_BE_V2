package com.example.repo_be_v2.domain.resume.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ResumePageRequest(

        @Min(0)
        int index,

        @NotNull
        String content

) {
}