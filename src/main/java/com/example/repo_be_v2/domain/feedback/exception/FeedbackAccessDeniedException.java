package com.example.repo_be_v2.domain.feedback.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class FeedbackAccessDeniedException extends REPOException {

    public FeedbackAccessDeniedException() {
        super(ErrorCode.FEEDBACK_ACCESS_DENIED);
    }
}
