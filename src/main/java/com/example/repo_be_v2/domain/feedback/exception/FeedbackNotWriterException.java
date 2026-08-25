package com.example.repo_be_v2.domain.feedback.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class FeedbackNotWriterException extends REPOException {

    public FeedbackNotWriterException() {
        super(ErrorCode.FEEDBACK_NOT_WRITER);
    }
}
