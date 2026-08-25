package com.example.repo_be_v2.domain.resume.presentation;

import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeAutoSaveRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeSaveRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeVisibilityRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.*;
import com.example.repo_be_v2.domain.resume.service.*;
import com.example.repo_be_v2.global.security.auth.AuthDetail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeGetService resumeGetService;
    private final ResumeSaveService resumeSaveService;
    private final ResumeAutoSaveService resumeAutoSaveService;
    private final ResumeSubmitService resumeSubmitService;
    private final ResumeCancelSubmitService resumeCancelSubmitService;
    private final ResumeVisibilityService resumeVisibilityService;

    //내 이력서 조회
    @GetMapping("/{resumeId}")
    public ResumeResponse getResume(
            @AuthenticationPrincipal AuthDetail auth,
            @PathVariable String resumeId
    ) {
        return resumeGetService.execute(auth.getId(), resumeId);
    }

    // 이력서 저장
    @PostMapping("/save")
    public ResumeSaveResponse saveResume(
            @AuthenticationPrincipal AuthDetail auth,
            @Valid @RequestBody ResumeSaveRequest request
    ) {
        return resumeSaveService.execute(auth.getId(), request);
    }

    // 이력서 자동저장
    @PostMapping("/auto-save")
    public ResumeAutoSaveResponse autoSaveResume(
            @AuthenticationPrincipal AuthDetail auth,
            @Valid @RequestBody ResumeAutoSaveRequest request
    ) {
        return resumeAutoSaveService.execute(auth.getId(), request);
    }

    // 이력서 제출
    @PostMapping("/submit")
    public ResumeSubmitResponse submitResume(
            @AuthenticationPrincipal AuthDetail auth
    ) {
        return resumeSubmitService.execute(auth.getId());
    }

    // 이력서 제출 취소
    @PostMapping("/submit/cancel")
    public ResumeSubmitResponse cancelSubmit(
            @AuthenticationPrincipal AuthDetail auth
    ) {
        return resumeCancelSubmitService.execute(auth.getId());
    }

    // 이력서 공개 여부 변경
    @PatchMapping("/visibility")
    public ResumeVisibilityResponse changeVisibility(
            @AuthenticationPrincipal AuthDetail auth,
            @Valid @RequestBody ResumeVisibilityRequest request
    ) {
        return resumeVisibilityService.execute(auth.getId(), request);
    }
}
