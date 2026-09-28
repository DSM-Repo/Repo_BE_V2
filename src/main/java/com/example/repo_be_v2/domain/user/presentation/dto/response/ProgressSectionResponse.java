package com.example.repo_be_v2.domain.user.presentation.dto.response;

import com.example.repo_be_v2.domain.user.domain.enums.ProgressSection;

public record ProgressSectionResponse(
        ProgressSection key,
        String name,
        boolean completed
) {
}
