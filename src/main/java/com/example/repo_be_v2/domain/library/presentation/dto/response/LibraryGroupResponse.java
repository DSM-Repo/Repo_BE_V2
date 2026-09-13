package com.example.repo_be_v2.domain.library.presentation.dto.response;

/**
 * 도서관 메인의 묶음 하나.
 *
 * date는 이력서가 공개된 학년도, year는 공개 당시 학년, cohort는 기수다.
 */
public record LibraryGroupResponse(
        int date,
        int year,
        int cohort
) {
}
