package com.example.repo_be_v2.domain.resume.presentation.dto.response;

import com.example.repo_be_v2.domain.resume.domain.ResumeProject;

import java.time.LocalDate;

public record ResumeProjectResponse(
        String name,
        String imageUrl,
        String summary,
        LocalDate startDate,
        LocalDate endDate
) {

    public static ResumeProjectResponse from(ResumeProject project) {
        if (project == null) {
            return null;
        }

        return new ResumeProjectResponse(
                project.getName(),
                project.getImageUrl(),
                project.getSummary(),
                project.getStartDate(),
                project.getEndDate()
        );
    }
}
