package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeSubmitResponse;
import com.example.repo_be_v2.domain.resume.service.support.ResumeReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResumeCancelSubmitService {

    private final ResumeRepository resumeRepository;
    private final ResumeReader resumeReader;

    //이력서 제출 취소
    @Transactional
    public ResumeSubmitResponse execute(Long userId) {
        resumeReader.getUser(userId);

        Resume resume = resumeReader.getResumeByUserId(userId);

        resume.cancelSubmit();

        Resume savedResume = resumeRepository.save(resume);

        return new ResumeSubmitResponse(
                savedResume.getId(),
                savedResume.getSubmissionStatus()
        );
    }
}
