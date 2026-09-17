package com.example.repo_be_v2.domain.library.presentation.dto.response;

import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumePageType;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeProjectResponse;

public record LibraryResumePageResponse(
        String id,
        int index,
        ResumePageType type,
        ResumeProjectResponse project,
        String content
) {

    public static LibraryResumePageResponse from(ResumePage page) {
        return new LibraryResumePageResponse(
                page.getId(),
                page.getIndex(),
                page.getType(),
                ResumeProjectResponse.from(page.getProject()),
                page.getContent()
        );
    }
}
