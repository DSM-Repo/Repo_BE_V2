package com.example.repo_be_v2.domain.image.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class UnsupportedImageTypeException extends REPOException {

    public UnsupportedImageTypeException() {
        super(ErrorCode.IMAGE_TYPE_NOT_SUPPORTED);
    }
}
