package com.example.repo_be_v2.domain.resume.presentation.dto.request;

import com.example.repo_be_v2.domain.resume.domain.enums.ResumePageType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResumePageRequest(

        //기존 페이지는 조회 때 받은 id를 그대로 돌려주고, 새 페이지는 비워 보내면 서버가 발급한다.
        @Size(max = 64, message = "페이지 id는 64자 이하여야 합니다.")
        String id,

        //최대 5쪽이라 0~4 중 하나다.
        @Min(value = 0, message = "페이지 위치는 0 이상이어야 합니다.")
        @Max(value = 4, message = "페이지 위치는 4 이하여야 합니다.")
        int index,

        @NotNull(message = "페이지 종류가 필요합니다.")
        ResumePageType type,

        //PROJECT 페이지에서만 채운다. 다른 종류로 보내면 무시한다.
        @Valid
        ResumeProjectRequest project,

        //마크다운 원문
        @NotNull(message = "페이지 내용이 필요합니다.")
        @Size(max = 10000, message = "페이지 내용은 10000자 이하여야 합니다.")
        String content

) {
}
