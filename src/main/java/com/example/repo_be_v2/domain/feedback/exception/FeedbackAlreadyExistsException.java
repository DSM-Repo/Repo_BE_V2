package com.example.repo_be_v2.domain.feedback.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class FeedbackAlreadyExistsException extends REPOException {

    public FeedbackAlreadyExistsException() {
        super(ErrorCode.FEEDBACK_ALREADY_EXISTS);
    }
}
