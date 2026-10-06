package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.exception.ResumeDeletedException;
import com.example.repo_be_v2.domain.resume.exception.ResumeNotFoundException;
import com.example.repo_be_v2.domain.resume.exception.ResumePageContentRequiredException;
import com.example.repo_be_v2.domain.resume.exception.ResumePagesRequiredException;
import com.example.repo_be_v2.domain.resume.exception.ResumeProjectNameRequiredException;
import com.example.repo_be_v2.domain.resume.exception.ResumeReleasedException;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeSaveRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeSubmitResponse;
import com.example.repo_be_v2.domain.resume.service.support.ResumeReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeSubmitService {

    private final ResumeRepository resumeRepository;
    private final ResumeReader resumeReader;

    /**
     * 이력서 제출
     *
     * 본문을 함께 받아 저장과 제출을 한 번에 한다.
     * 이미 제출한 이력서를 고쳐서 다시 낼 때도 같은 요청을 쓴다.
     *
     * 제출을 취소하고 저장한 뒤 다시 제출하는 세 번의 요청으로 나누면
     * 중간에 실패했을 때 이력서가 작성 중으로 남아 선생님 화면에 미제출로 보인다.
     * 한 요청 안에서 끝내 그런 상태가 생기지 않게 한다.
     */
    @Transactional
    public ResumeSubmitResponse execute(Long userId, ResumeSaveRequest request) {
        resumeReader.getUser(userId);

        Resume resume = resumeReader.getResumeByUserId(userId);

        //공개된 이력서는 본문이 어떻든 거절한다. 본문 검증이 앞서면 그쪽 400이 409를 가린다.
        resume.validateSubmittable();

        List<ResumePage> pages = resumeReader.toResumePages(resume, request.pages());

        validateRequiredFields(pages);

        resume.submit(
                request.introduce(),
                request.email(),
                request.skills(),
                request.portfolioUrl(),
                request.profileImageUrl(),
                pages,
                LocalDateTime.now()
        );

        /*
         * 위에서 확인한 상태가 쓰기 직전까지 유지됐을 때만 저장한다.
         *
         * 읽고 쓰는 사이에 선생님이 이력서를 공개해버릴 수 있다.
         * 그냥 save()로 덮어쓰면 통째로 교체되기 때문에
         * 방금 올라간 공개 상태가 상태값까지 포함해 사라진다.
         */
        Resume savedResume = resumeRepository.replaceIfSubmittable(resume)
                .orElseThrow(() -> conflictException(userId));

        return new ResumeSubmitResponse(
                savedResume.getId(),
                savedResume.getSubmissionStatus()
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

    /**
     * 제출 전 필수 항목 검증
     *
     * 저장된 이력서가 아니라 이번에 제출하려는 본문을 검사한다.
     * 검증을 통과하지 못하면 아무것도 덮어쓰지 않고 돌려보낸다.
     */
    private void validateRequiredFields(List<ResumePage> pages) {
        if (pages == null || pages.isEmpty()) {
            throw new ResumePagesRequiredException();
        }

        boolean hasEmptyPage = pages.stream()
                .anyMatch(page -> page.getContent() == null
                        || page.getContent().isBlank());

        if (hasEmptyPage) {
            throw new ResumePageContentRequiredException();
        }

        //프로젝트 페이지는 이름이 있어야 도서관에서 무슨 프로젝트인지 알아볼 수 있다.
        boolean hasUnnamedProject = pages.stream()
                .filter(ResumePage::isProject)
                .anyMatch(page -> page.getProject() == null
                        || !page.getProject().hasName());

        if (hasUnnamedProject) {
            throw new ResumeProjectNameRequiredException();
        }
    }
}
