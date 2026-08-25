package com.example.repo_be_v2.domain.feedback.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

//명세의 요청 본문이 snake_case라 @JsonProperty로 매핑한다.
public record FeedbackUpdateRequest(
        @JsonProperty("document_id")
        @NotBlank(message = "document_id가 비어있습니다")
        String documentId,

        @JsonProperty("element_id")
        @NotBlank(message = "element_id가 비어있습니다")
        String elementId,

        @NotBlank(message = "comment가 비어있습니다")
        String comment
) {
}
