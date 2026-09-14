package com.example.repo_be_v2.domain.library.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class LibraryResumeNotFoundException extends REPOException {

    public LibraryResumeNotFoundException() {
        super(ErrorCode.LIBRARY_RESUME_NOT_FOUND);
    }
}
