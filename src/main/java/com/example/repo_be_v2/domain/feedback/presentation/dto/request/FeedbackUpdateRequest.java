package com.example.repo_be_v2.domain.feedback.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * 피드백 수정 요청
 *
 * 대상 문서는 수정할 피드백이 이미 들고 있으므로 받지 않는다.
 * 요청으로 문서를 지정하면 피드백의 resumeId와 다른 문서를 가리킬 수 있다.
 */
public record FeedbackUpdateRequest(
        @NotBlank(message = "pageId가 비어있습니다")
        String pageId,

        @NotNull(message = "x가 비어있습니다")
        @PositiveOrZero(message = "x는 0 이상이어야 합니다")
        Double x,

        @NotNull(message = "y가 비어있습니다")
        @PositiveOrZero(message = "y는 0 이상이어야 합니다")
        Double y,

        @NotBlank(message = "comment가 비어있습니다")
        String comment
) {
}
