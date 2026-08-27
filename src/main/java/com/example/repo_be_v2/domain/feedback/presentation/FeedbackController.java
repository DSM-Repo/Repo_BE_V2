package com.example.repo_be_v2.domain.feedback.presentation;

import com.example.repo_be_v2.domain.feedback.presentation.dto.request.FeedbackApplyRequest;
import com.example.repo_be_v2.domain.feedback.presentation.dto.request.FeedbackCreateRequest;
import com.example.repo_be_v2.domain.feedback.presentation.dto.request.FeedbackUpdateRequest;
import com.example.repo_be_v2.domain.feedback.presentation.dto.response.*;
import com.example.repo_be_v2.domain.feedback.service.*;
import com.example.repo_be_v2.global.config.OpenApiConfig;
import com.example.repo_be_v2.global.security.auth.AuthDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "이력서 피드백 작성, 조회 및 반영 API")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
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
    @Operation(summary = "문서별 피드백 목록 조회", description = "문서 전체 또는 특정 페이지의 피드백 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "피드백 목록 조회 성공", useReturnTypeSchema = true)
    public FeedbackListResponse getFeedbacks(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Parameter(description = "이력서 문서 ID", example = "66c73ec4c92f1d2d087e9012")
            @RequestParam String documentId,
            @Parameter(description = "페이지 인덱스. 생략하면 문서 전체 피드백을 조회합니다.", example = "0")
            @RequestParam(required = false) Integer pageIndex
    ) {
        return feedbackListService.execute(auth.getId(), documentId, pageIndex);
    }

    // 피드백 단건 조회
    @GetMapping("/{feedbackId}")
    @Operation(summary = "피드백 단건 조회")
    @ApiResponse(responseCode = "200", description = "피드백 조회 성공", useReturnTypeSchema = true)
    public FeedbackResponse getFeedback(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Parameter(description = "피드백 ID", example = "66c74063c92f1d2d087e9013")
            @PathVariable String feedbackId
    ) {
        return feedbackGetService.execute(auth.getId(), feedbackId);
    }

    // 피드백 추가 (선생님 권한)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "피드백 추가", description = "선생님이 이력서의 특정 요소에 피드백을 작성합니다.")
    @ApiResponse(responseCode = "201", description = "피드백 생성 성공", useReturnTypeSchema = true)
    public FeedbackCreateResponse createFeedback(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Valid @RequestBody FeedbackCreateRequest request
    ) {
        return feedbackCreateService.execute(auth.getId(), request);
    }

    // 피드백 수정 (선생님 권한, 본인이 작성한 피드백만)
    @PatchMapping("/{feedbackId}")
    @Operation(summary = "피드백 수정", description = "선생님이 본인이 작성한 피드백을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "피드백 수정 성공", useReturnTypeSchema = true)
    public FeedbackUpdateResponse updateFeedback(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Parameter(description = "피드백 ID", example = "66c74063c92f1d2d087e9013")
            @PathVariable String feedbackId,
            @Valid @RequestBody FeedbackUpdateRequest request
    ) {
        return feedbackUpdateService.execute(auth.getId(), feedbackId, request);
    }

    // 피드백 삭제 (선생님 권한, 본인이 작성한 피드백만)
    @DeleteMapping("/{feedbackId}")
    @Operation(summary = "피드백 삭제", description = "선생님이 본인이 작성한 피드백을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "피드백 삭제 성공", useReturnTypeSchema = true)
    public FeedbackDeleteResponse deleteFeedback(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Parameter(description = "피드백 ID", example = "66c74063c92f1d2d087e9013")
            @PathVariable String feedbackId
    ) {
        return feedbackDeleteService.execute(auth.getId(), feedbackId);
    }

    // 피드백 반영 처리 (학생, 본인 이력서의 피드백만)
    @PatchMapping("/{feedbackId}/complete")
    @Operation(summary = "피드백 반영 완료", description = "학생이 본인 이력서의 피드백을 반영 완료 상태로 변경합니다.")
    @ApiResponse(responseCode = "200", description = "피드백 반영 완료 처리 성공", useReturnTypeSchema = true)
    public FeedbackStatusResponse completeFeedback(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Parameter(description = "피드백 ID", example = "66c74063c92f1d2d087e9013")
            @PathVariable String feedbackId
    ) {
        return feedbackCompleteService.execute(auth.getId(), feedbackId);
    }

    // 피드백 반영 취소 (학생, 본인 이력서의 피드백만)
    @PatchMapping("/{feedbackId}/pending")
    @Operation(summary = "피드백 반영 취소", description = "학생이 본인 이력서의 피드백을 미반영 상태로 되돌립니다.")
    @ApiResponse(responseCode = "200", description = "피드백 반영 취소 성공", useReturnTypeSchema = true)
    public FeedbackStatusResponse pendingFeedback(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Parameter(description = "피드백 ID", example = "66c74063c92f1d2d087e9013")
            @PathVariable String feedbackId
    ) {
        return feedbackPendingService.execute(auth.getId(), feedbackId);
    }

    // 피드백 일괄 반영 (학생)
    @PatchMapping("/apply")
    @Operation(summary = "피드백 일괄 반영", description = "학생이 여러 피드백을 한 번에 반영 또는 반영 취소 처리합니다.")
    @ApiResponse(responseCode = "200", description = "피드백 일괄 처리 완료", useReturnTypeSchema = true)
    public FeedbackApplyResponse applyFeedbacks(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Valid @RequestBody FeedbackApplyRequest request
    ) {
        return feedbackApplyService.execute(auth.getId(), request);
    }
}
