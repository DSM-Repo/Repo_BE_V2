package com.example.repo_be_v2.global.error;

/**
 * 검증에 걸린 입력값 하나.
 *
 * field는 요청 본문의 필드 이름이나 쿼리 파라미터 이름이다.
 */
public record FieldErrorResponse(
        String field,
        String message
) {
}
