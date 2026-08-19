package com.example.repo_be_v2.global.error.exception;

import lombok.Getter;

@Getter
public class REPOException extends RuntimeException {

    private final ErrorCode errorCode;

    public REPOException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }

    public REPOException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    public REPOException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getErrorMessage(), cause);
        this.errorCode = errorCode;
    }
}
