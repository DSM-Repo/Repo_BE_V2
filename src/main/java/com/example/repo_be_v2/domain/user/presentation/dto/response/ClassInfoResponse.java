package com.example.repo_be_v2.domain.user.presentation.dto.response;

public record ClassInfoResponse(
        int grade,
        int classNumber,
        int number,
        String schoolNumber
) {
}
