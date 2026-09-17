package com.example.repo_be_v2.domain.resume.presentation.dto.request;

import com.example.repo_be_v2.domain.resume.domain.enums.ResumePageType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ResumePageRequest(

        //기존 페이지는 조회 때 받은 id를 그대로 돌려주고, 새 페이지는 비워 보내면 서버가 발급한다.
        String id,

        @Min(0)
        int index,

        @NotNull
        ResumePageType type,

        //PROJECT 페이지에서만 채운다. 다른 종류로 보내면 무시한다.
        @Valid
        ResumeProjectRequest project,

        //마크다운 원문
        @NotNull
        String content

) {
}
