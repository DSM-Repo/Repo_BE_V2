package com.example.repo_be_v2.global.s3;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class S3UploadFailedException extends REPOException {

    public S3UploadFailedException(Throwable cause) {
        super(ErrorCode.IMAGE_UPLOAD_FAILED, cause);
    }
}
