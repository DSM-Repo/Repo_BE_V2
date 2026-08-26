package com.example.repo_be_v2.domain.feedback.presentation.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FeedbackApplyRequest(
        @NotEmpty(message = "feedbackIds가 비어있습니다")
        List<String> feedbackIds,

        //true면 반영(COMPLETED), false면 반영 취소(PENDING)로 일괄 처리한다.
        @NotNull(message = "applied가 비어있습니다")
        Boolean applied
) {
}
