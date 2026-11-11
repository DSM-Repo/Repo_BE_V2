package com.example.repo_be_v2.domain.user.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class SignUpRoleNotAllowedException extends REPOException {

    public SignUpRoleNotAllowedException() {
        super(ErrorCode.SIGNUP_ROLE_NOT_ALLOWED);
    }
}
