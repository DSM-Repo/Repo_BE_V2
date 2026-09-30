package com.example.repo_be_v2.domain.history.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class HistoryNotFoundException extends REPOException {

    public HistoryNotFoundException() {
        super(ErrorCode.HISTORY_NOT_FOUND);
    }
}
