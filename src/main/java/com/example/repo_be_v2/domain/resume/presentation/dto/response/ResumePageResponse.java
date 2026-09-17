package com.example.repo_be_v2.domain.resume.presentation.dto.response;

import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumePageType;

public record ResumePageResponse(
        String id,
        int index,
        ResumePageType type,
        ResumeProjectResponse project,
        String content
) {

    public static ResumePageResponse from(ResumePage page) {
        return new ResumePageResponse(
                page.getId(),
                page.getIndex(),
                page.getType(),
                ResumeProjectResponse.from(page.getProject()),
                page.getContent()
        );
    }
}
