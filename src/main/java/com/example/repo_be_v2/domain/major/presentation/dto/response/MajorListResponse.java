package com.example.repo_be_v2.domain.major.presentation.dto.response;

import java.util.List;

public record MajorListResponse(
        List<MajorResponse> majors,
        int numberOfData
) {
}
