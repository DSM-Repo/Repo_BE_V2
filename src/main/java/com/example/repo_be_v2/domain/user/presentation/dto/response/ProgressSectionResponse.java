package com.example.repo_be_v2.domain.user.presentation.dto.response;

import com.example.repo_be_v2.domain.user.domain.enums.ProgressSection;

/**
 * 도넛 그래프 한 칸.
 *
 * percent는 칸 안의 항목을 세어 낸 값이고, completed는 그 값이 100일 때만 true다.
 * 칸을 부분 채움으로 그릴 수 있게 둘 다 내려준다.
 */
public record ProgressSectionResponse(
        ProgressSection key,
        String name,
        int percent,
        boolean completed
) {
}
