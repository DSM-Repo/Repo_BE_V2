package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeAutoSaveRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumePageRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeSaveRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeVisibilityRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.*;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.global.error.exception.ErrorCode;
import com.example.repo_be_v2.global.error.exception.REPOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    //내 이력서 조회
    public ResumeResponse getResume(
            Long userId,
            String resumeId
    ) {
        User user = getUser(userId);
        Resume resume = getResumeByIdAndUserId(resumeId, userId);

        return toResumeResponse(user, resume);
    }

    /**
     * 이력서 수동 저장
     *
     * 이력서가 없으면 새로 만들고,
     * 이미 있으면 기존 이력서를 수정한다.
     */
    public ResumeSaveResponse saveResume(
            Long userId,
            ResumeSaveRequest request
    ) {
        getUser(userId);

        List<ResumePage> pages = toResumePages(request.pages());
        LocalDateTime savedAt = LocalDateTime.now();

        Resume resume = findOrCreateResume(userId, request, pages, savedAt);
        Resume savedResume = resumeRepository.save(resume);

        return new ResumeSaveResponse(
                savedResume.getId(),
                savedResume.getSavedAt()
        );
    }

    //이력서 자동 저장, 자동 저장에서는 pages만 변경한다.

    public ResumeAutoSaveResponse autoSaveResume(
            Long userId,
            ResumeAutoSaveRequest request
    ) {
        getUser(userId);

        Resume resume = getResumeByUserId(userId);
        List<ResumePage> pages = toResumePages(request.pages());
        LocalDateTime savedAt = LocalDateTime.now();

        resume.autoSave(pages, savedAt);

        Resume savedResume = resumeRepository.save(resume);

        return new ResumeAutoSaveResponse(
                savedResume.getId(),
                savedResume.getSavedAt(),
                true
        );
    }

    //이력서 최종 제출

    public ResumeSubmitResponse submitResume(Long userId) {
        getUser(userId);

        Resume resume = getResumeByUserId(userId);

        validateNotAlreadySubmitted(resume);
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

        Resume resume = getResumeByUserId(userId);

        resume.changeVisibility(request.isPublic(), LocalDateTime.now());

        Resume savedResume = resumeRepository.save(resume);

        return new ResumeVisibilityResponse(
                savedResume.isPublic()
        );
    }

    //이력서 제출 취소

    public ResumeSubmitResponse cancelSubmit(Long userId) {
        getUser(userId);

        Resume resume = getResumeByUserId(userId);

        resume.cancelSubmit();

        Resume savedResume = resumeRepository.save(resume);

        return new ResumeSubmitResponse(
                savedResume.getId(),
                savedResume.getSubmissionStatus()
        );
    }

    //MySQL에 실제 사용자가 존재하는지 확인

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new REPOException(ErrorCode.USER_NOT_FOUND));
    }

    //유저 소유의 이력서를 id 기준으로 조회
    private Resume getResumeByIdAndUserId(String resumeId, Long userId) {
        return resumeRepository
                .findByIdAndUserId(resumeId, userId)
                .orElseThrow(() -> new REPOException(ErrorCode.RESUME_NOT_FOUND));
    }

    //유저 소유의 이력서를 조회

    private Resume getResumeByUserId(Long userId) {
        return resumeRepository
                .findByUserId(userId)
                .orElseThrow(() -> new REPOException(ErrorCode.RESUME_NOT_FOUND));
    }

    //기존 이력서가 있으면 수정, 없으면 새로 생성

    private Resume findOrCreateResume(
            Long userId,
            ResumeSaveRequest request,
            List<ResumePage> pages,
            LocalDateTime savedAt
    ) {
        return resumeRepository
                .findByUserId(userId)
                .map(existingResume -> updateResume(existingResume, request, pages, savedAt))
                .orElseGet(() -> createResume(userId, request, pages, savedAt));
    }

    private Resume updateResume(
            Resume existingResume,
            ResumeSaveRequest request,
            List<ResumePage> pages,
            LocalDateTime savedAt
    ) {
        existingResume.save(
                request.introduce(),
                request.portfolioUrl(),
                pages,
                savedAt
        );

        return existingResume;
    }

    private Resume createResume(
            Long userId,
            ResumeSaveRequest request,
            List<ResumePage> pages,
            LocalDateTime savedAt
    ) {
        return Resume.create(
                userId,
                request.introduce(),
                request.portfolioUrl(),
                pages,
                savedAt
        );
    }

    //요청 DTO의 페이지 목록을 도메인 객체로 변환

    private List<ResumePage> toResumePages(List<ResumePageRequest> pageRequests) {
        return pageRequests.stream()
                .map(page -> new ResumePage(
                        page.index(),
                        page.content()
                ))
                .toList();
    }

    //이력서 도메인을 응답 DTO로 변환

    private ResumeResponse toResumeResponse(User user, Resume resume) {
        return new ResumeResponse(
                resume.getId(),
                user.getStudentName(),
                resume.getIntroduce(),
                resume.getPortfolioUrl(),
                resume.isPublic(),
                null,
                user.getStudentMajor(),
                resume.getSubmissionStatus(),
                resume.getSavedAt(),
                toResumePageResponses(resume.getPages())
        );
    }

    private List<ResumePageResponse> toResumePageResponses(List<ResumePage> pages) {
        return pages.stream()
                .map(page -> new ResumePageResponse(
                        page.getIndex(),
                        page.getContent()
                ))
                .toList();
    }

    //제출 전 필수 항목 검증
    private void validateRequiredFields(Resume resume) {
        validatePagesExist(resume);
        validateNoEmptyPageContent(resume);
    }

    private void validatePagesExist(Resume resume) {
        if (resume.getPages() == null || resume.getPages().isEmpty()) {
            throw new REPOException(ErrorCode.RESUME_PAGES_REQUIRED);
        }
    }

    private void validateNoEmptyPageContent(Resume resume) {
        boolean hasEmptyPage = resume.getPages()
                .stream()
                .anyMatch(page ->
                        page.getContent() == null
                                || page.getContent().isBlank()
                );

        if (hasEmptyPage) {
            throw new REPOException(ErrorCode.RESUME_PAGE_CONTENT_REQUIRED);
        }
    }

    private void validateNotAlreadySubmitted(Resume resume) {
        if (resume.getSubmissionStatus() == ResumeSubmissionStatus.SUBMITTED) {
            throw new REPOException(ErrorCode.RESUME_ALREADY_SUBMITTED);
        }
    }
}
