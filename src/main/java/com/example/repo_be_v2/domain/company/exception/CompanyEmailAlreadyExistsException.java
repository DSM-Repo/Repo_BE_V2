package com.example.repo_be_v2.domain.company.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class CompanyEmailAlreadyExistsException extends REPOException {

    public CompanyEmailAlreadyExistsException() {
        super(ErrorCode.COMPANY_EMAIL_ALREADY_EXISTS);
    }
}
