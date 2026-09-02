package com.example.repo_be_v2.domain.feedback.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record FeedbackCreateRequest(
        @NotBlank(message = "documentId가 비어있습니다")
        String documentId,

        @NotBlank(message = "pageId가 비어있습니다")
        String pageId,

        //페이지 좌상단 기준 절대 px 좌표
        @NotNull(message = "x가 비어있습니다")
        @PositiveOrZero(message = "x는 0 이상이어야 합니다")
        Double x,

        @NotNull(message = "y가 비어있습니다")
        @PositiveOrZero(message = "y는 0 이상이어야 합니다")
        Double y,

        @NotBlank(message = "comment 내용이 비어있습니다")
        String comment
) {
}
