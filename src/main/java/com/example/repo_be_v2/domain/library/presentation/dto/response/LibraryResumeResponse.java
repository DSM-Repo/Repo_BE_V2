package com.example.repo_be_v2.domain.library.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 도서관에서 보는 이력서 한 건.
 *
 * 작성 중 상태(submissionStatus, isPublic)는 담지 않는다.
 * 도서관에는 공개된 이력서만 올라오므로 볼 필요가 없는 값이다.
 */
public record LibraryResumeResponse(
        String resumeId,
        Long studentId,
        String name,
        String studentNumber,
        String email,
        String majorName,
        String profileImageUrl,
        String introduce,
        List<String> skills,
        String portfolioUrl,
        int date,
        int year,
        int cohort,
        LocalDateTime releasedAt,
        List<LibraryResumePageResponse> pages
) {
}
