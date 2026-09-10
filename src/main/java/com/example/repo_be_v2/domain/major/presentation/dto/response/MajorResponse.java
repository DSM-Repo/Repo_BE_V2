package com.example.repo_be_v2.domain.major.presentation.dto.response;

import com.example.repo_be_v2.domain.major.domain.Major;

public record MajorResponse(
        Long majorId,
        String name
) {

    public static MajorResponse from(Major major) {
        return new MajorResponse(
                major.getId(),
                major.getName()
        );
    }
}
