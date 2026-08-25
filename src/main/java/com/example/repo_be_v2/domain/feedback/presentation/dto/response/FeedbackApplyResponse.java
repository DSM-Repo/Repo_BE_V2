package com.example.repo_be_v2.domain.feedback.presentation.dto.response;

import java.util.List;

public record FeedbackApplyResponse(
        int successCount,
        List<FeedbackApplyFailureResponse> failed
) {
}
