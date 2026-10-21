package com.example.repo_be_v2.domain.user.presentation.dto.response;

/**
 * 홈(마이페이지) 상단 프로필 영역에 필요한 값.
 *
 * 화면의 직군 자리에는 전공 이름(major)이 들어간다. 따로 직군 필드는 두지 않는다.
 *
 * 이력서 완료율(GET /user/progress)과 알림(GET /alram)은 각자 따로 조회한다.
 */
public record UserMypageResponse(
        String name,
        String profileImageUrl,
        String introduce,
        String major,
        ClassInfoResponse classInfo
) {
}
