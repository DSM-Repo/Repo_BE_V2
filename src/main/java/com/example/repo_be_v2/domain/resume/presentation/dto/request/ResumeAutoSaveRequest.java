package com.example.repo_be_v2.domain.resume.presentation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ResumeAutoSaveRequest(
        @NotNull @Valid
        List<ResumePageRequest> pages
) {
}
