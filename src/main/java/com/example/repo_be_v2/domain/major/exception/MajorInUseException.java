package com.example.repo_be_v2.domain.major.exception;

import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;

/**
 * 학생이 쓰고 있는 전공은 지우지 않는다.
 * 몇 명이 쓰는지 함께 알려줘야 교사가 다음 행동을 정할 수 있어 인원수를 메시지에 담는다.
 */
public class MajorInUseException extends REPOException {

    public MajorInUseException(long studentCount) {
        super(
                ErrorCode.MAJOR_IN_USE,
                ErrorCode.MAJOR_IN_USE.getErrorMessage() + " (해당 전공 학생 " + studentCount + "명)"
        );
    }
}
