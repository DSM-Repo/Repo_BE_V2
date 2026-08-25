package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeVisibilityRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeVisibilityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResumeVisibilityService {

    private final ResumeRepository resumeRepository;
    private final ResumeReader resumeReader;

    //이력서 공개 여부 변경
    @Transactional
    public ResumeVisibilityResponse execute(Long userId, ResumeVisibilityRequest request) {
        resumeReader.getUser(userId);

        Resume resume = resumeReader.getResumeByUserId(userId);

        resume.changeVisibility(request.isPublic(), LocalDateTime.now());

        Resume savedResume = resumeRepository.save(resume);

        return new ResumeVisibilityResponse(savedResume.isPublic());
    }
}
