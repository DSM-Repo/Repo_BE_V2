package com.example.repo_be_v2.domain.user.presentation.dto.response;

public record TokenResponse(
        String tokenType,
        String accessToken,
        long expiresIn
) {
}
