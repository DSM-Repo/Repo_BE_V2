package com.example.repo_be_v2.domain.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 홈 화면 도넛 그래프의 세 칸.
 *
 * 이력서 본문이 HTML 덩어리라 "활동"과 "프로젝트"를 내용으로 구분할 수 없다.
 * 대신 이력서 작성 단계에 하나씩 대응시킨다.
 * 생성 → 페이지 작성 → 제출 순서로 차오른다.
 */
@Getter
@AllArgsConstructor
public enum ProgressSection {

    PROFILE("내 정보"),
    ACTIVITY("활동"),
    PROJECT("프로젝트");

    private final String displayName;
}
