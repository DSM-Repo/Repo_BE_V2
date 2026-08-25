package com.example.repo_be_v2.domain.resume.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class ResumeNotEditableException extends REPOException {

    public ResumeNotEditableException() {
        super(ErrorCode.RESUME_NOT_EDITABLE);
    }
}
