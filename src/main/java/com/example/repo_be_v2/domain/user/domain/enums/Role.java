package com.example.repo_be_v2.domain.user.domain.enums;

public enum Role {
    STUDENT,
    TEACHER,

    //도서관만 열람하는 기업. tbl_user가 아니라 tbl_company에 있어 회원가입으로는 만들 수 없다.
    COMPANY,
}
