package com.example.repo_be_v2.domain.resume.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ResumeSaveRequest(
    String introduce,
    String portfolioUrl,
    @NotNull
    List<ResumePageRequest> pages
) {
}
