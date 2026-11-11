package com.example.repo_be_v2.domain.company.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class CompanyCodeInvalidException extends REPOException {

    public CompanyCodeInvalidException() {
        super(ErrorCode.COMPANY_CODE_INVALID);
    }
}
