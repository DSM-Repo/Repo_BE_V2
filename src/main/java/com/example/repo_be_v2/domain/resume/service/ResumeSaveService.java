package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.exception.ResumeDeletedException;
import com.example.repo_be_v2.domain.resume.exception.ResumeNotFoundException;
import com.example.repo_be_v2.domain.resume.exception.ResumeReleasedException;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeSaveRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeSaveResponse;
import com.example.repo_be_v2.domain.resume.service.support.ResumeReader;
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
     * 이력서 저장
     *
     * 이력서가 없으면 새로 만들고,
     * 이미 있으면 기존 이력서를 수정한다.
     * 페이지 id를 물려받아야 하므로 기존 이력서를 먼저 조회한 뒤 페이지를 변환한다.
     *
     * 제출한 뒤에도 저장할 수 있다. 제출 상태는 그대로 두고 본문만 갱신한다.
     * 고치려고 제출을 취소했다가 다시 내는 왕복을 없애려는 것이다.
     * 공개된 이력서는 막는데, 그 판정은 도메인이 한다.
     *
     * 자동 저장도 이 경로를 그대로 쓴다.
     */
    @Transactional
    public ResumeSaveResponse execute(Long userId, ResumeSaveRequest request) {
        resumeReader.getUser(userId);

        Resume existingResume = resumeRepository.findByUserId(userId).orElse(null);
        List<ResumePage> pages = resumeReader.toResumePages(existingResume, request.pages());
        LocalDateTime savedAt = LocalDateTime.now();

        if (existingResume == null) {
            return toResponse(resumeRepository.save(create(userId, request, pages, savedAt)));
        }

        existingResume.save(
                request.introduce(),
                request.email(),
                request.skills(),
                request.portfolioUrl(),
                request.profileImageUrl(),
                pages,
                savedAt
        );

        /*
         * 읽고 쓰는 사이에 선생님이 이력서를 공개해버릴 수 있다.
         * save()로 덮어쓰면 문서가 통째로 교체되어 방금 올라간 공개가 사라지므로,
         * 저장된 상태가 아직 손댈 수 있을 때만 교체한다.
         */
        return toResponse(
                resumeRepository.replaceIfWritable(existingResume)
                        .orElseThrow(() -> conflictException(userId))
        );
    }

    private ResumeSaveResponse toResponse(Resume resume) {
        return new ResumeSaveResponse(
                resume.getId(),
                resume.getSavedAt()
        );
    }

    /**
     * 조건부 저장이 밀렸을 때 왜 밀렸는지 알아낸다.
     *
     * 저장 직전에 상태가 바뀐 경우라 지금 저장된 값을 다시 읽어 사유를 정한다.
     */
    private RuntimeException conflictException(Long userId) {
        ResumeSubmissionStatus status = resumeRepository.findByUserId(userId)
                .map(Resume::getSubmissionStatus)
                .orElse(null);

        if (status == null) {
            return new ResumeNotFoundException();
        }

        if (status == ResumeSubmissionStatus.DELETED) {
            return new ResumeDeletedException();
        }

        return new ResumeReleasedException();
    }

    //첫 저장. 아직 문서가 없으므로 조건을 걸 것도 없이 새로 넣는다.
    private Resume create(
            Long userId,
            ResumeSaveRequest request,
            List<ResumePage> pages,
            LocalDateTime savedAt
    ) {
        return Resume.create(
                userId,
                request.introduce(),
                request.email(),
                request.skills(),
                request.portfolioUrl(),
                request.profileImageUrl(),
                pages,
                savedAt
        );
    }
}
