package com.example.repo_be_v2.domain.resume.domain.repository;

import java.time.LocalDateTime;

/**
 * 도서관 목록·검색이 쓰는 공개 이력서 요약.
 *
 * 학년도 묶음과 검색 대상 추리기에는 userId와 releasedAt만 필요하다.
 * 페이지 본문(HTML)까지 전부 읽어오지 않도록 이 두 필드만 프로젝션한다.
 */
public interface PublicResumeSummary {

    Long getUserId();

    LocalDateTime getReleasedAt();
}
