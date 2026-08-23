package com.example.repo_be_v2.domain.user.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class EmailVerificationCodeExpiredException extends REPOException {

    public EmailVerificationCodeExpiredException() {
        super(ErrorCode.EMAIL_VERIFICATION_CODE_EXPIRED);
    }
}
