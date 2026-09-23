package com.example.repo_be_v2.domain.notification.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class NotificationNotFoundException extends REPOException {

    public NotificationNotFoundException() {
        super(ErrorCode.NOTIFICATION_NOT_FOUND);
    }
}
