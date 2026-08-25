package com.example.repo_be_v2.domain.resume.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class ResumeAlreadySubmittedException extends REPOException {

    public ResumeAlreadySubmittedException() {
        super(ErrorCode.RESUME_ALREADY_SUBMITTED);
    }
}
