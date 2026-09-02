package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumePageResponse;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeResponse;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.resume.service.support.ResumeReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeGetService {

    private final ResumeReader resumeReader;

    //내 이력서 조회
    @Transactional(readOnly = true)
    public ResumeResponse execute(Long userId, String resumeId) {
        User user = resumeReader.getUser(userId);
        Resume resume = resumeReader.getResumeByIdAndUserId(resumeId, userId);

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
                        page.getId(),
                        page.getIndex(),
                        page.getContent()
                ))
                .toList();
    }
}
