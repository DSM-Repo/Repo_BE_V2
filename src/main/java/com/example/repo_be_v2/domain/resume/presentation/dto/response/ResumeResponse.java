package com.example.repo_be_v2.domain.resume.presentation.dto.response;

import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 내 이력서 조회 응답.
 *
 * 저장 요청에 담은 값을 그대로 돌려주고,
 * 서버가 가진 값(이름, 전공, 프로필 사진, 상태)만 더한다.
 * 프런트는 받은 그대로 고쳐서 다시 저장 요청으로 보내면 된다.
 */
public record ResumeResponse(
        String id,
        String name,
        String introduce,
        String email,
        List<String> skills,
        String portfolioUrl,
        boolean isPublic,
        String profileImageUrl,
        String majorName,
        ResumeSubmissionStatus submissionStatus,
        LocalDateTime savedAt,
        List<ResumePageResponse> pages
) {
}
