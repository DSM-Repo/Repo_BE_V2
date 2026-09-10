package com.example.repo_be_v2.domain.major.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class MajorAlreadyExistsException extends REPOException {

    public MajorAlreadyExistsException() {
        super(ErrorCode.MAJOR_ALREADY_EXISTS);
    }
}
