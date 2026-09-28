package com.example.repo_be_v2.domain.resume.domain.enums;

/**
 * 이력서 페이지의 종류.
 *
 * 에디터가 페이지마다 다른 템플릿을 그리므로 서버도 종류를 알고 있어야 한다.
 * 본문(content)은 어느 종류든 마크다운 한 덩어리고,
 * 템플릿 머리말에 들어가는 값만 종류별로 다르다.
 */
public enum ResumePageType {

    //이름, 전공, 이메일, 기술스택이 붙는 첫 장. 본문은 활동 영역이다.
    PROFILE,

    //프로젝트 한 건을 담는 장. project 블록이 함께 온다.
    PROJECT,

    //머리말 없이 본문만 있는 장.
    FREE
}
