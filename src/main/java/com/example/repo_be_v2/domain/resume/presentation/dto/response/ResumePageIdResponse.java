package com.example.repo_be_v2.domain.resume.presentation.dto.response;

import com.example.repo_be_v2.domain.resume.domain.ResumePage;

import java.util.List;

/**
 * 저장된 페이지의 위치와 id.
 *
 * 새 페이지의 id는 서버가 발급하므로, 화면이 다음 저장부터 그 id를 보낼 수 있게 저장 응답에 실어 준다.
 * 이 값이 없으면 화면이 id를 알려고 이력서를 한 번 더 조회해야 한다.
 */
public record ResumePageIdResponse(
        String id,
        int index
) {

    public static List<ResumePageIdResponse> listOf(List<ResumePage> pages) {
        return pages.stream()
                .map(page -> new ResumePageIdResponse(page.getId(), page.getIndex()))
                .toList();
    }
}
