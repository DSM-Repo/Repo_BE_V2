package com.example.repo_be_v2.domain.feedback.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class FeedbackElementNotFoundException extends REPOException {

    public FeedbackElementNotFoundException() {
        super(ErrorCode.FEEDBACK_ELEMENT_NOT_FOUND);
    }
}
