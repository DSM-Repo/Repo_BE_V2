package com.example.repo_be_v2.domain.company.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class CompanyNotFoundException extends REPOException {

    public CompanyNotFoundException() {
        super(ErrorCode.COMPANY_NOT_FOUND);
    }
}
