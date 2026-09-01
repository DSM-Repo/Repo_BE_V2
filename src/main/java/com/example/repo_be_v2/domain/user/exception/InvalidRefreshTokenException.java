package com.example.repo_be_v2.domain.user.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class InvalidRefreshTokenException extends REPOException {

    public InvalidRefreshTokenException() {
        super(ErrorCode.INVALID_REFRESH_TOKEN);
    }
}
