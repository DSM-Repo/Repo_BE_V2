package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.exception.ResumeAlreadySubmittedException;
import com.example.repo_be_v2.domain.resume.exception.ResumePageContentRequiredException;
import com.example.repo_be_v2.domain.resume.exception.ResumePagesRequiredException;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeSubmitResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResumeSubmitService {

    private final ResumeRepository resumeRepository;
    private final ResumeReader resumeReader;

    //이력서 최종 제출
    @Transactional
    public ResumeSubmitResponse execute(Long userId) {
        resumeReader.getUser(userId);

        Resume resume = resumeReader.getResumeByUserId(userId);

        validateNotAlreadySubmitted(resume);
        validateRequiredFields(resume);

        resume.submit(LocalDateTime.now());

        Resume savedResume = resumeRepository.save(resume);

        return new ResumeSubmitResponse(
                savedResume.getId(),
                savedResume.getSubmissionStatus()
        );
    }

    private void validateNotAlreadySubmitted(Resume resume) {
        if (resume.getSubmissionStatus() == ResumeSubmissionStatus.SUBMITTED) {
            throw new ResumeAlreadySubmittedException();
        }
    }

    //제출 전 필수 항목 검증
    private void validateRequiredFields(Resume resume) {
        if (resume.getPages() == null || resume.getPages().isEmpty()) {
            throw new ResumePagesRequiredException();
        }

        boolean hasEmptyPage = resume.getPages()
                .stream()
                .anyMatch(page -> page.getContent() == null
                        || page.getContent().isBlank());

        if (hasEmptyPage) {
            throw new ResumePageContentRequiredException();
        }
    }
}
