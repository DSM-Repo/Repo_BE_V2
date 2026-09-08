package com.example.repo_be_v2.domain.major.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class MajorNotFoundException extends REPOException {

    public MajorNotFoundException() {
        super(ErrorCode.MAJOR_NOT_FOUND);
    }
}
