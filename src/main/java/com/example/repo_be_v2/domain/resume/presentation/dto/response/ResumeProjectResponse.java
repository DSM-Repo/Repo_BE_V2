package com.example.repo_be_v2.domain.resume.presentation.dto.response;

import com.example.repo_be_v2.domain.resume.domain.ResumeProject;

import java.time.LocalDate;
import java.util.function.UnaryOperator;

public record ResumeProjectResponse(
        String name,
        String imageUrl,
        String summary,
        LocalDate startDate,
        LocalDate endDate
) {

    //이미지는 저장된 URL로는 열 수 없어 toViewUrl로 바꿔 담는다.
    public static ResumeProjectResponse from(ResumeProject project, UnaryOperator<String> toViewUrl) {
        if (project == null) {
            return null;
        }

        return new ResumeProjectResponse(
                project.getName(),
                toViewUrl.apply(project.getImageUrl()),
                project.getSummary(),
                project.getStartDate(),
                project.getEndDate()
        );
    }
}
