package com.example.repo_be_v2.domain.company.presentation.dto.response;

import java.util.List;

public record CompanyListResponse(
        List<CompanyResponse> companies,
        int numberOfData
) {
}
