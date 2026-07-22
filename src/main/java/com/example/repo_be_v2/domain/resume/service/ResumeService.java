package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.enms.ResumeSubmissionStatus;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeAutoSaveRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeSaveRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeVisibilityRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.*;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    // 내 이력서 조회
    public ResumeResponse getResume(
            Long userId,
            String resumeId
    ) {
        User user = getUser(userId);

        Resume resume = resumeRepository
                .findByIdAndUserId(resumeId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("이력서를 찾을 수 없습니다.")
                );

        List<ResumePageResponse> pages = resume.getPages()
                .stream()
                .map(page -> new ResumePageResponse(
                        page.getIndex(),
                        page.getContent()
                ))
                .toList();

        return new ResumeResponse(
                resume.getId(),
                user.getStudentName(),
                resume.getIntroduce(),
                resume.getPortfolioUrl(),
                resume.isPublic(),
                null,
                user.getStudentMajor(),
                pages
        );
    }

    //이력서 수동 저장 이력서가 없으면 새로 만들고, 이미 있으면 기존 이력서를 수정한다.
    public ResumeSaveResponse saveResume(
            Long userId,
            ResumeSaveRequest request
    ) {
        getUser(userId);

        List<ResumePage> pages = request.pages()
                .stream()
                .map(page -> new ResumePage(
                        page.index(),
                        page.content()
                ))
                .toList();

        LocalDateTime savedAt = LocalDateTime.now();

        Resume resume = resumeRepository
                .findByUserId(userId)
                .map(existingResume -> {
                    existingResume.save(
                            request.introduce(),
                            request.portfolioUrl(),
                            pages,
                            savedAt
                    );

                    return existingResume;
                })
                .orElseGet(() ->
                        Resume.create(
                                userId,
                                request.introduce(),
                                request.portfolioUrl(),
                                pages,
                                savedAt
                        )
                );

        Resume savedResume = resumeRepository.save(resume);

        return new ResumeSaveResponse(
                savedResume.getId(),
                savedResume.getSavedAt()
        );
    }

    //이력서 자동저장 자동 저장에서는 pages만 변경한다.

    public ResumeAutoSaveResponse autoSaveResume(
            Long userId,
            ResumeAutoSaveRequest request
    ) {
        getUser(userId);

        Resume resume = resumeRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "자동 저장할 이력서가 없습니다."
                        )
                );

        List<ResumePage> pages = request.pages()
                .stream()
                .map(page -> new ResumePage(
                        page.index(),
                        page.content()
                ))
                .toList();

        LocalDateTime savedAt = LocalDateTime.now();

        resume.autoSave(pages, savedAt);

        Resume savedResume = resumeRepository.save(resume);

        return new ResumeAutoSaveResponse(
                savedResume.getId(),
                savedResume.getSavedAt(),
                true
        );
    }

    // 이력서 최종 제출
    public ResumeSubmitResponse submitResume(Long userId) {
        getUser(userId);

        Resume resume = resumeRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "제출할 이력서를 찾을 수 없습니다."
                        )
                );

        if (resume.getSubmissionStatus() == ResumeSubmissionStatus.SUBMITTED) {
            throw new IllegalStateException(
                    "이미 제출된 이력서입니다."
            );
        }

        validateRequiredFields(resume);

        resume.submit(LocalDateTime.now());

        Resume savedResume = resumeRepository.save(resume);

        return new ResumeSubmitResponse(
                savedResume.getId(),
                savedResume.getSubmissionStatus()
        );
    }

    //이력서 공개 여부 변경
    public ResumeVisibilityResponse changeVisibility(
            Long userId,
            ResumeVisibilityRequest request
    ) {
        getUser(userId);

        Resume resume = resumeRepository
                .findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "이력서를 찾을 수 없습니다."
                        )
                );

        resume.changeVisibility(request.isPublic());

        Resume savedResume = resumeRepository.save(resume);

        return new ResumeVisibilityResponse(
                savedResume.isPublic()
        );
    }

    //MySQL에 실제 사용자가 존재하는지 확인
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "사용자를 찾을 수 없습니다."
                        )
                );
    }

    //제출 전 필수 항목 검증
    private void validateRequiredFields(Resume resume) {
        if (resume.getPages() == null || resume.getPages().isEmpty()) {
            throw new IllegalArgumentException(
                    "이력서 페이지를 작성해야 합니다."
            );
        }

        boolean hasEmptyPage = resume.getPages()
                .stream()
                .anyMatch(page ->
                        page.getContent() == null
                                || page.getContent().isBlank()
                );

        if (hasEmptyPage) {
            throw new IllegalArgumentException(
                    "작성되지 않은 이력서 페이지가 있습니다."
            );
        }
    }
}