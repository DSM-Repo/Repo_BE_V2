package com.example.repo_be_v2.domain.resume.presentation.dto.response;

import java.time.LocalDateTime;

public record ResumeSaveResponse(
        String resumeId,
        LocalDateTime savedAt
) {
}