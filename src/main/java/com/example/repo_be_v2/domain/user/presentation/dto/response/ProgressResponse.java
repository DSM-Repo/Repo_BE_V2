package com.example.repo_be_v2.domain.user.presentation.dto.response;

import java.util.List;

public record ProgressResponse(
        int totalPercent,
        List<ProgressSectionResponse> sections
) {
}
