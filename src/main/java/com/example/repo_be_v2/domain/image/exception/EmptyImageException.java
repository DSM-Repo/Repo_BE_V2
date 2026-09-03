package com.example.repo_be_v2.domain.image.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class EmptyImageException extends REPOException {

    public EmptyImageException() {
        super(ErrorCode.IMAGE_EMPTY);
    }
}
