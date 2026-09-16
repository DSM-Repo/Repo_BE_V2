package com.example.repo_be_v2.domain.user.presentation.dto.response;

/**
 * 홈(마이페이지) 화면 한 번에 필요한 값.
 *
 * position과 profileImageUrl은 아직 저장하는 곳이 없어 항상 null이다.
 * 화면 자리를 먼저 잡아두고, tbl_user에 컬럼이 생기면 값만 채운다.
 */
public record UserMypageResponse(
        String name,
        String position,
        String profileImageUrl,
        String introduce,
        String major,
        ClassInfoResponse classInfo,
        ProgressResponse progress
) {
}
