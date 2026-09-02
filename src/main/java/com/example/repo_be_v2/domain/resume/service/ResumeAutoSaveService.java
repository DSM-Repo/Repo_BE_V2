package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeAutoSaveRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeAutoSaveResponse;
import com.example.repo_be_v2.domain.resume.service.support.ResumeReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeAutoSaveService {

    private final ResumeRepository resumeRepository;
    private final ResumeReader resumeReader;

    //이력서 자동 저장, 자동 저장에서는 pages만 변경한다.
    @Transactional
    public ResumeAutoSaveResponse execute(Long userId, ResumeAutoSaveRequest request) {
        resumeReader.getUser(userId);

        Resume resume = resumeReader.getResumeByUserId(userId);
        List<ResumePage> pages = resumeReader.toResumePages(resume, request.pages());
        LocalDateTime savedAt = LocalDateTime.now();

        resume.autoSave(pages, savedAt);

        Resume savedResume = resumeRepository.save(resume);

        return new ResumeAutoSaveResponse(
                savedResume.getId(),
                savedResume.getSavedAt(),
                true
        );
    }
}
