package com.example.repo_be_v2.domain.resume.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ResumeSaveResponse(
        String resumeId,
        LocalDateTime savedAt,
        List<ResumePageIdResponse> pages
) {
}