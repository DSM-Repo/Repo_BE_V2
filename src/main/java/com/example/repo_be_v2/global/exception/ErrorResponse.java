package com.example.repo_be_v2.global.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String message,
        Map<String, String> errors
) {
}
