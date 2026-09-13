package com.example.repo_be_v2.domain.library.presentation.dto.response;

import java.util.List;

public record LibrarySearchResponse(
        List<LibrarySearchItemResponse> content,
        long totalElements
) {
}
