package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.feedback.service.FeedbackSyncService;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeSaveRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeSaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeSaveService {

    private final ResumeRepository resumeRepository;
    private final ResumeReader resumeReader;
    private final FeedbackSyncService feedbackSyncService;

    /**
     * 이력서 수동 저장
     *
     * 이력서가 없으면 새로 만들고,
     * 이미 있으면 기존 이력서를 수정한다.
     */
    @Transactional
    public ResumeSaveResponse execute(Long userId, ResumeSaveRequest request) {
        resumeReader.getUser(userId);

        List<ResumePage> pages = resumeReader.toResumePages(request.pages());
        LocalDateTime savedAt = LocalDateTime.now();

        Resume resume = findOrCreateResume(userId, request, pages, savedAt);
        Resume savedResume = resumeRepository.save(resume);

        //이력서에서 사라지거나 옮겨진 객체의 피드백을 정리한다.
        feedbackSyncService.execute(savedResume);

        return new ResumeSaveResponse(
                savedResume.getId(),
                savedResume.getSavedAt()
        );
    }

    //기존 이력서가 있으면 수정, 없으면 새로 생성
    private Resume findOrCreateResume(
            Long userId,
            ResumeSaveRequest request,
            List<ResumePage> pages,
            LocalDateTime savedAt
    ) {
        return resumeRepository.findByUserId(userId)
                .map(existingResume -> {
                    existingResume.save(
                            request.introduce(),
                            request.portfolioUrl(),
                            pages,
                            savedAt
                    );
                    return existingResume;
                })
                .orElseGet(() -> Resume.create(
                        userId,
                        request.introduce(),
                        request.portfolioUrl(),
                        pages,
                        savedAt
                ));
    }
}
