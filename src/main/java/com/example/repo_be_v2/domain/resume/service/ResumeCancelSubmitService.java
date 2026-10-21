package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
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
        ResumeSubmissionStatus readStatus = resume.getSubmissionStatus();

        resume.cancelSubmit();

        /*
         * 읽고 쓰는 사이에 선생님이 공개해버릴 수 있다.
         * 문서를 통째로 저장하면 그 공개를 되돌리므로 상태가 그대로일 때만 상태 필드를 바꾼다.
         */
        Resume savedResume = resumeRepository.updateStatusIfUnchanged(resume, readStatus)
                .orElseThrow(() -> resumeReader.statusChangedException(userId));

        return new ResumeSubmitResponse(
                savedResume.getId(),
                savedResume.getSubmissionStatus()
        );
    }
}
