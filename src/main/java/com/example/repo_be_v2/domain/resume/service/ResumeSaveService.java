package com.example.repo_be_v2.domain.resume.service;

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

    /**
     * 이력서 수동 저장
     *
     * 이력서가 없으면 새로 만들고,
     * 이미 있으면 기존 이력서를 수정한다.
     * 페이지 id를 물려받아야 하므로 기존 이력서를 먼저 조회한 뒤 페이지를 변환한다.
     */
    @Transactional
    public ResumeSaveResponse execute(Long userId, ResumeSaveRequest request) {
        resumeReader.getUser(userId);

        Resume existingResume = resumeRepository.findByUserId(userId).orElse(null);
        List<ResumePage> pages = resumeReader.toResumePages(existingResume, request.pages());
        LocalDateTime savedAt = LocalDateTime.now();

        Resume resume = saveOrCreate(existingResume, userId, request, pages, savedAt);
        Resume savedResume = resumeRepository.save(resume);

        return new ResumeSaveResponse(
                savedResume.getId(),
                savedResume.getSavedAt()
        );
    }

    //기존 이력서가 있으면 수정, 없으면 새로 생성
    private Resume saveOrCreate(
            Resume existingResume,
            Long userId,
            ResumeSaveRequest request,
            List<ResumePage> pages,
            LocalDateTime savedAt
    ) {
        if (existingResume == null) {
            return Resume.create(
                    userId,
                    request.introduce(),
                    request.portfolioUrl(),
                    pages,
                    savedAt
            );
        }

        existingResume.save(
                request.introduce(),
                request.portfolioUrl(),
                pages,
                savedAt
        );

        return existingResume;
    }
}
