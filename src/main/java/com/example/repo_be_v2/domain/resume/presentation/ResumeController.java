package com.example.repo_be_v2.domain.resume.presentation;

import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeAutoSaveRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeSaveRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeVisibilityRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.*;
import com.example.repo_be_v2.domain.resume.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resume")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    //내 이력서 조회
    @GetMapping("/{resumeId}")
    public ResumeResponse getResume(
            @RequestParam Long userId,
            @PathVariable String resumeId
    ) {
        return resumeService.getResume(userId, resumeId);
    }

    // 이력서 저장
    @PostMapping("/save")
    public ResumeSaveResponse saveResume(
            @RequestParam Long userId,
            @Valid @RequestBody ResumeSaveRequest request
    ) {
        return resumeService.saveResume(userId, request);
    }

    // 이력서 자동저장
    @PostMapping("/auto-save")
    public ResumeAutoSaveResponse autoSaveResume(
            @RequestParam Long userId,
            @Valid @RequestBody ResumeAutoSaveRequest request
    ) {
        return resumeService.autoSaveResume(userId, request);
    }

    // 이력서 제출
    @PostMapping("/submit")
    public ResumeSubmitResponse submitResume(
            @RequestParam Long userId
    ) {
        return resumeService.submitResume(userId);
    }

    // 이력서 제출 취소
    @PostMapping("/submit/cancel")
    public ResumeSubmitResponse cancelSubmit(
            @RequestParam Long userId
    ) {
        return resumeService.cancelSubmit(userId);
    }

    // 이력서 공개 여부 변경
    @PatchMapping("/visibility")
    public ResumeVisibilityResponse changeVisibility(
            @RequestParam Long userId,
            @Valid @RequestBody ResumeVisibilityRequest request
    ) {
        return resumeService.changeVisibility(userId, request);
    }
}