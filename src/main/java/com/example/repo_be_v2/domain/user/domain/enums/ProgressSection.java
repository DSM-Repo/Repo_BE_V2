package com.example.repo_be_v2.domain.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 홈 화면 도넛 그래프의 세 칸.
 *
 * 이력서 본문이 마크다운 덩어리라 칸을 내용으로 구분할 수 없다.
 * 대신 머리말 값과 페이지 종류로 칸을 가른다.
 *
 * weight는 가운데 퍼센트를 낼 때 쓰는 비중이다.
 * 이력서에서 분량을 차지하는 활동·프로젝트를 내 정보보다 무겁게 본다.
 */
@Getter
@AllArgsConstructor
public enum ProgressSection {

    PROFILE("내 정보", 20),
    ACTIVITY("활동", 40),
    PROJECT("프로젝트", 40);

    private final String displayName;
    private final int weight;
}
