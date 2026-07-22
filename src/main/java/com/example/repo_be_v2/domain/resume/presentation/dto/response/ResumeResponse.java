package com.example.repo_be_v2.domain.resume.presentation.dto.response;

import java.util.List;

public record ResumeResponse(
        String id,
        String name,
        String introduce,
        String portfolioUrl,
        boolean isPublic,
        String profileImageUrl,
        String majorName,
        List<ResumePageResponse> pages
) {
}
