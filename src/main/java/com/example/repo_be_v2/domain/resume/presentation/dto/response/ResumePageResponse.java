package com.example.repo_be_v2.domain.resume.presentation.dto.response;

import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumePageType;

import java.util.function.UnaryOperator;

public record ResumePageResponse(
        String id,
        int index,
        ResumePageType type,
        ResumeProjectResponse project,
        String content
) {

    public static ResumePageResponse from(ResumePage page, UnaryOperator<String> toViewUrl) {
        return new ResumePageResponse(
                page.getId(),
                page.getIndex(),
                page.getType(),
                ResumeProjectResponse.from(page.getProject(), toViewUrl),
                page.getContent()
        );
    }
}
