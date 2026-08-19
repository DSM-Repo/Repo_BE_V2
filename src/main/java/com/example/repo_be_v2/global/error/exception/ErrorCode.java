package com.example.repo_be_v2.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // common
    BAD_REQUEST(400, "잘못된 요청입니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "권한이 없습니다."),
    METHOD_NOT_ALLOWED(405, "지원하지 않는 메서드 형식입니다."),
    INTERNAL_SERVER_ERROR(500, "내부 서버 오류가 발생했습니다."),

    // user
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),

    // resume
    RESUME_NOT_FOUND(404, "이력서를 찾을 수 없습니다."),
    RESUME_ALREADY_SUBMITTED(409, "이미 제출된 이력서입니다."),
    RESUME_PAGES_REQUIRED(400, "이력서 페이지를 작성해야 합니다."),
    RESUME_PAGE_CONTENT_REQUIRED(400, "작성되지 않은 이력서 페이지가 있습니다."),
    RESUME_NOT_EDITABLE(400, "제출된 이력서는 수정할 수 없습니다."),
    RESUME_NOT_SUBMITTED(400, "제출된 이력서가 아닙니다."),
    ;

    private final int statusCode;
    private final String errorMessage;
}
