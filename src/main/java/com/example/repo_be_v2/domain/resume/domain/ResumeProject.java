package com.example.repo_be_v2.domain.resume.domain;

import com.example.repo_be_v2.domain.resume.exception.ResumeProjectPeriodInvalidException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 프로젝트 페이지의 머리말.
 *
 * 작성 중에는 비어 있을 수 있어 어떤 값도 필수가 아니다.
 * 제출 시점에 이름이 있는지만 확인한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumeProject {

    private String name;

    private String imageUrl;

    private String summary;

    private LocalDate startDate;

    private LocalDate endDate;

    public static ResumeProject of(
            String name,
            String imageUrl,
            String summary,
            LocalDate startDate,
            LocalDate endDate
    ) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new ResumeProjectPeriodInvalidException();
        }

        ResumeProject project = new ResumeProject();

        project.name = name;
        project.imageUrl = imageUrl;
        project.summary = summary;
        project.startDate = startDate;
        project.endDate = endDate;

        return project;
    }

    public boolean hasName() {
        return name != null && !name.isBlank();
    }
}
