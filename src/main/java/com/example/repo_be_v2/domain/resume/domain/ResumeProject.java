package com.example.repo_be_v2.domain.resume.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프로젝트 페이지의 머리말.
 *
 * 작성 중에는 비어 있을 수 있어 어떤 값도 필수가 아니다.
 * 제출 시점에 이름이 있는지만 확인한다.
 *
 * 기간은 "2026.03", "진행 중"처럼 학생이 쓴 그대로 보여주므로 날짜가 아닌 문자열로 둔다.
 * 날짜로 해석하지 않으니 시작일·종료일의 앞뒤도 따지지 않는다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResumeProject {

    private String name;

    private String imageUrl;

    private String summary;

    private String startDate;

    private String endDate;

    public static ResumeProject of(
            String name,
            String imageUrl,
            String summary,
            String startDate,
            String endDate
    ) {
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
