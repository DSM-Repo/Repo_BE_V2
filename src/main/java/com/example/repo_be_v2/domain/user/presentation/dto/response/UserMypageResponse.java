package com.example.repo_be_v2.domain.user.presentation.dto.response;

import com.example.repo_be_v2.domain.notification.presentation.dto.response.NotificationResponse;

import java.util.List;

/**
 * 홈(마이페이지) 화면 한 번에 필요한 값.
 *
 * 화면의 직군 자리에는 전공 이름(major)이 들어간다. 따로 직군 필드는 두지 않는다.
 *
 * 홈 화면 오른쪽 알림 목록도 같이 내려준다. 화면 하나에 요청 하나면 되게 하려는 것이고,
 * 내용은 GET /alram과 같다.
 */
public record UserMypageResponse(
        String name,
        String profileImageUrl,
        String introduce,
        String major,
        ClassInfoResponse classInfo,
        ProgressResponse progress,
        List<NotificationResponse> notifications
) {
}
