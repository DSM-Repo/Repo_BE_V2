package com.example.repo_be_v2.domain.company.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class CompanyEmailNotAllowedException extends REPOException {

    public CompanyEmailNotAllowedException() {
        super(ErrorCode.COMPANY_EMAIL_NOT_ALLOWED);
    }
}
