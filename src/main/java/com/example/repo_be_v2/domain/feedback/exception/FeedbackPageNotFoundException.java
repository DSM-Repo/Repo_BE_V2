package com.example.repo_be_v2.domain.feedback.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class FeedbackPageNotFoundException extends REPOException {

    public FeedbackPageNotFoundException() {
        super(ErrorCode.FEEDBACK_PAGE_NOT_FOUND);
    }
}
