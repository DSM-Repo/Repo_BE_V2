package com.example.repo_be_v2.domain.resume.domain.repository;

import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;

import java.time.LocalDateTime;

/**
 * 선생님의 제출 현황 조회가 쓰는 이력서 요약.
 *
 * 제출 여부와 최근 갱신 시각만 필요하므로 페이지 본문(HTML)은 읽지 않는다.
 */
public interface ResumeStatusSummary {

    String getId();

    Long getUserId();

    ResumeSubmissionStatus getSubmissionStatus();

    LocalDateTime getSavedAt();

    LocalDateTime getSubmittedAt();
}
