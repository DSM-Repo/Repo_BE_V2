package com.example.repo_be_v2.global.error;

import com.example.repo_be_v2.global.error.exception.ErrorCode;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        Integer status,
        LocalDateTime timestamp,
        String description
) {

    public static ErrorResponse of(ErrorCode errorCode, String description) {
        return new ErrorResponse(
                errorCode.getErrorMessage(),
                errorCode.getStatusCode(),
                LocalDateTime.now(),
                description
        );
    }

    public static ErrorResponse of(int statusCode, String description) {
        return new ErrorResponse(
                description,
                statusCode,
                LocalDateTime.now(),
                description
        );
    }
}
