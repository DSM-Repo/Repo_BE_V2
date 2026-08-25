package com.example.repo_be_v2.domain.feedback.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class FeedbackApplyLimitExceededException extends REPOException {

    public FeedbackApplyLimitExceededException() {
        super(ErrorCode.FEEDBACK_APPLY_LIMIT_EXCEEDED);
    }
}
