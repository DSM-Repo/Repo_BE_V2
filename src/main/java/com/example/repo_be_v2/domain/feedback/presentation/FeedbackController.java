package com.example.repo_be_v2.domain.feedback.presentation;

import com.example.repo_be_v2.domain.feedback.presentation.dto.request.FeedbackApplyRequest;
import com.example.repo_be_v2.domain.feedback.presentation.dto.request.FeedbackCreateRequest;
import com.example.repo_be_v2.domain.feedback.presentation.dto.request.FeedbackUpdateRequest;
import com.example.repo_be_v2.domain.feedback.presentation.dto.response.*;
import com.example.repo_be_v2.domain.feedback.service.*;
import com.example.repo_be_v2.global.security.auth.AuthDetail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackCreateService feedbackCreateService;
    private final FeedbackUpdateService feedbackUpdateService;
    private final FeedbackDeleteService feedbackDeleteService;
    private final FeedbackGetService feedbackGetService;
    private final FeedbackListService feedbackListService;
    private final FeedbackCompleteService feedbackCompleteService;
    private final FeedbackPendingService feedbackPendingService;
    private final FeedbackApplyService feedbackApplyService;

    // 문서별 피드백 목록 조회, 학생은 본인 이력서의 피드백만 볼 수 있다.
    @GetMapping
    public FeedbackListResponse getFeedbacks(
            @AuthenticationPrincipal AuthDetail auth,
            @RequestParam String documentId,
            @RequestParam(required = false) Integer pageIndex
    ) {
        return feedbackListService.execute(auth.getId(), documentId, pageIndex);
    }

    // 피드백 단건 조회
    @GetMapping("/{feedbackId}")
    public FeedbackResponse getFeedback(
            @AuthenticationPrincipal AuthDetail auth,
            @PathVariable String feedbackId
    ) {
        return feedbackGetService.execute(auth.getId(), feedbackId);
    }

    // 피드백 추가 (선생님 권한)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FeedbackCreateResponse createFeedback(
            @AuthenticationPrincipal AuthDetail auth,
            @Valid @RequestBody FeedbackCreateRequest request
    ) {
        return feedbackCreateService.execute(auth.getId(), request);
    }

    // 피드백 수정 (선생님 권한, 본인이 작성한 피드백만)
    @PatchMapping("/{feedbackId}")
    public FeedbackUpdateResponse updateFeedback(
            @AuthenticationPrincipal AuthDetail auth,
            @PathVariable String feedbackId,
            @Valid @RequestBody FeedbackUpdateRequest request
    ) {
        return feedbackUpdateService.execute(auth.getId(), feedbackId, request);
    }

    // 피드백 삭제 (선생님 권한, 본인이 작성한 피드백만)
    @DeleteMapping("/{feedbackId}")
    public FeedbackDeleteResponse deleteFeedback(
            @AuthenticationPrincipal AuthDetail auth,
            @PathVariable String feedbackId
    ) {
        return feedbackDeleteService.execute(auth.getId(), feedbackId);
    }

    // 피드백 반영 처리 (학생, 본인 이력서의 피드백만)
    @PatchMapping("/{feedbackId}/complete")
    public FeedbackStatusResponse completeFeedback(
            @AuthenticationPrincipal AuthDetail auth,
            @PathVariable String feedbackId
    ) {
        return feedbackCompleteService.execute(auth.getId(), feedbackId);
    }

    // 피드백 반영 취소 (학생, 본인 이력서의 피드백만)
    @PatchMapping("/{feedbackId}/pending")
    public FeedbackStatusResponse pendingFeedback(
            @AuthenticationPrincipal AuthDetail auth,
            @PathVariable String feedbackId
    ) {
        return feedbackPendingService.execute(auth.getId(), feedbackId);
    }

    // 피드백 일괄 반영 (학생)
    @PatchMapping("/apply")
    public FeedbackApplyResponse applyFeedbacks(
            @AuthenticationPrincipal AuthDetail auth,
            @Valid @RequestBody FeedbackApplyRequest request
    ) {
        return feedbackApplyService.execute(auth.getId(), request);
    }
}
