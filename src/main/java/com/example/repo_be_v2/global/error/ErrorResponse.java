package com.example.repo_be_v2.global.error;

import com.example.repo_be_v2.global.error.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 모든 에러 응답의 공통 형식.
 *
 * code는 화면에서 분기할 때 쓰는 값이다. message는 문구가 바뀔 수 있으니 비교에 쓰지 않는다.
 * errors는 입력값 검증에 걸렸을 때만 채워지고, 그 외에는 빈 배열이다.
 */
public record ErrorResponse(
        String message,
        Integer status,
        LocalDateTime timestamp,
        String description,
        String code,
        List<FieldErrorResponse> errors
) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return of(errorCode, errorCode.getErrorMessage());
    }

    public static ErrorResponse of(ErrorCode errorCode, String description) {
        return of(errorCode, description, List.of());
    }

    public static ErrorResponse of(ErrorCode errorCode, String description, List<FieldErrorResponse> errors) {
        return new ErrorResponse(
                errorCode.getErrorMessage(),
                errorCode.getStatusCode(),
                LocalDateTime.now(),
                description,
                errorCode.name(),
                errors
        );
    }
}
