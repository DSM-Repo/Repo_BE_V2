package com.example.repo_be_v2.domain.library.presentation.dto.response;

public record LibrarySearchItemResponse(
        Long studentId,
        String studentName,
        String major
) {
}
