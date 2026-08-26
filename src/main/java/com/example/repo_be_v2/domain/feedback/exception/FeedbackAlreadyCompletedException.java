package com.example.repo_be_v2.domain.feedback.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class FeedbackAlreadyCompletedException extends REPOException {

    public FeedbackAlreadyCompletedException() {
        super(ErrorCode.FEEDBACK_ALREADY_COMPLETED);
    }
}
