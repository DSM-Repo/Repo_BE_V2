package com.example.repo_be_v2.domain.resume.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class ResumePageContentRequiredException extends REPOException {

    public ResumePageContentRequiredException() {
        super(ErrorCode.RESUME_PAGE_CONTENT_REQUIRED);
    }
}
