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
    INVALID_CREDENTIALS(401, "이메일 또는 비밀번호가 올바르지 않습니다."),
    EMAIL_ALREADY_EXISTS(409, "이미 사용 중인 이메일입니다."),
    EMAIL_VERIFICATION_REQUEST_LIMIT(429, "인증 메일은 5분 후에 다시 요청할 수 있습니다."),
    EMAIL_VERIFICATION_CODE_EXPIRED(410, "인증 코드가 만료되었거나 존재하지 않습니다."),
    EMAIL_VERIFICATION_CODE_MISMATCH(400, "인증 코드가 올바르지 않습니다."),
    EMAIL_NOT_VERIFIED(403, "이메일 인증이 필요합니다."),
    EMAIL_SEND_FAILED(500, "인증 메일 전송에 실패했습니다."),
    TEACHER_PERMISSION_REQUIRED(403, "선생님 권한이 필요합니다."),

    // resume
    RESUME_NOT_FOUND(404, "이력서를 찾을 수 없습니다."),
    RESUME_ALREADY_SUBMITTED(409, "이미 제출된 이력서입니다."),
    RESUME_PAGES_REQUIRED(400, "이력서 페이지를 작성해야 합니다."),
    RESUME_PAGE_CONTENT_REQUIRED(400, "작성되지 않은 이력서 페이지가 있습니다."),
    RESUME_NOT_EDITABLE(400, "제출된 이력서는 수정할 수 없습니다."),
    RESUME_NOT_SUBMITTED(400, "제출된 이력서가 아닙니다."),
    RESUME_DELETED(410, "삭제된 이력서입니다."),
    RESUME_ACCESS_DENIED(403, "이력서를 볼 수 있는 권한이 없습니다."),

    // feedback
    FEEDBACK_NOT_FOUND(404, "해당 피드백을 찾을 수 없습니다."),
    FEEDBACK_PAGE_NOT_FOUND(404, "피드백을 추가할 페이지가 없습니다."),
    FEEDBACK_ACCESS_DENIED(403, "해당 피드백에 접근할 권한이 없습니다."),
    FEEDBACK_NOT_WRITER(403, "본인이 작성한 피드백만 수정하거나 삭제할 수 있습니다."),
    FEEDBACK_NOT_OWNER(403, "본인 이력서의 피드백만 반영 처리할 수 있습니다."),
    FEEDBACK_ALREADY_COMPLETED(409, "이미 처리한 피드백입니다."),
    FEEDBACK_NOT_COMPLETED(409, "아직 반영 처리되지 않은 피드백입니다."),
    FEEDBACK_APPLY_LIMIT_EXCEEDED(400, "한 번에 처리할 수 있는 피드백 수를 초과했습니다."),
    ;

    private final int statusCode;
    private final String errorMessage;
}
