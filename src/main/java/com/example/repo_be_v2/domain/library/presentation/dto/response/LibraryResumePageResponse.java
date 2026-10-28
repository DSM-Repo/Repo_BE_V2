package com.example.repo_be_v2.domain.library.presentation.dto.response;

import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumePageType;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeProjectResponse;
import com.example.repo_be_v2.global.s3.MarkdownUrls;

import java.util.function.UnaryOperator;

public record LibraryResumePageResponse(
        String id,
        int index,
        ResumePageType type,
        ResumeProjectResponse project,
        String content
) {

    public static LibraryResumePageResponse from(ResumePage page, UnaryOperator<String> toViewUrl) {
        return new LibraryResumePageResponse(
                page.getId(),
                page.getIndex(),
                page.getType(),
                ResumeProjectResponse.from(page.getProject(), toViewUrl),
                MarkdownUrls.replace(page.getContent(), toViewUrl)
        );
    }
}
