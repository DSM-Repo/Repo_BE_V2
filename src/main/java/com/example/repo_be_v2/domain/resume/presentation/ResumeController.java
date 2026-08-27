package com.example.repo_be_v2.domain.resume.presentation;

import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeAutoSaveRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeSaveRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeVisibilityRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.*;
import com.example.repo_be_v2.domain.resume.service.*;
import com.example.repo_be_v2.global.security.auth.AuthDetail;
import com.example.repo_be_v2.global.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
@Tag(name = "Resume", description = "이력서 조회, 저장, 제출 및 공개 설정 API")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class ResumeController {

    private final ResumeGetService resumeGetService;
    private final ResumeSaveService resumeSaveService;
    private final ResumeAutoSaveService resumeAutoSaveService;
    private final ResumeSubmitService resumeSubmitService;
    private final ResumeCancelSubmitService resumeCancelSubmitService;
    private final ResumeVisibilityService resumeVisibilityService;

    //내 이력서 조회
    @GetMapping("/{resumeId}")
    @Operation(summary = "내 이력서 조회", description = "이력서 ID와 로그인한 사용자 ID를 함께 검증하여 본인의 이력서를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "이력서 조회 성공", useReturnTypeSchema = true)
    public ResumeResponse getResume(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Parameter(description = "조회할 이력서 ID", example = "66c73ec4c92f1d2d087e9012")
            @PathVariable String resumeId
    ) {
        return resumeGetService.execute(auth.getId(), resumeId);
    }

    // 이력서 저장
    @PostMapping("/save")
    @Operation(summary = "이력서 저장", description = "이력서가 없으면 생성하고, 있으면 기존 이력서를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "이력서 저장 성공", useReturnTypeSchema = true)
    public ResumeSaveResponse saveResume(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Valid @RequestBody ResumeSaveRequest request
    ) {
        return resumeSaveService.execute(auth.getId(), request);
    }

    // 이력서 자동저장
    @PostMapping("/auto-save")
    @Operation(summary = "이력서 자동 저장", description = "작성 중인 이력서 페이지 내용을 자동 저장합니다.")
    @ApiResponse(responseCode = "200", description = "자동 저장 성공", useReturnTypeSchema = true)
    public ResumeAutoSaveResponse autoSaveResume(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Valid @RequestBody ResumeAutoSaveRequest request
    ) {
        return resumeAutoSaveService.execute(auth.getId(), request);
    }

    // 이력서 제출
    @PostMapping("/submit")
    @Operation(summary = "이력서 제출", description = "작성 중인 이력서를 제출 상태로 변경합니다.")
    @ApiResponse(responseCode = "200", description = "이력서 제출 성공", useReturnTypeSchema = true)
    public ResumeSubmitResponse submitResume(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth
    ) {
        return resumeSubmitService.execute(auth.getId());
    }

    // 이력서 제출 취소
    @PostMapping("/submit/cancel")
    @Operation(summary = "이력서 제출 취소", description = "제출한 이력서를 다시 작성 중 상태로 변경합니다.")
    @ApiResponse(responseCode = "200", description = "제출 취소 성공", useReturnTypeSchema = true)
    public ResumeSubmitResponse cancelSubmit(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth
    ) {
        return resumeCancelSubmitService.execute(auth.getId());
    }

    // 이력서 공개 여부 변경
    @PatchMapping("/visibility")
    @Operation(summary = "이력서 공개 여부 변경", description = "제출된 이력서의 공개 여부를 변경합니다.")
    @ApiResponse(responseCode = "200", description = "공개 여부 변경 성공", useReturnTypeSchema = true)
    public ResumeVisibilityResponse changeVisibility(
            @Parameter(hidden = true) @AuthenticationPrincipal AuthDetail auth,
            @Valid @RequestBody ResumeVisibilityRequest request
    ) {
        return resumeVisibilityService.execute(auth.getId(), request);
    }
}
