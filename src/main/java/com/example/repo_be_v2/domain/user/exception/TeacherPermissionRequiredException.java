package com.example.repo_be_v2.domain.user.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

public class TeacherPermissionRequiredException extends REPOException {

    public TeacherPermissionRequiredException() {
        super(ErrorCode.TEACHER_PERMISSION_REQUIRED);
    }
}
