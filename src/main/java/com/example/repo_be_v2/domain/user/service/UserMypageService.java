package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.user.domain.enums.ProgressSection;
import com.example.repo_be_v2.domain.user.presentation.dto.response.ClassInfoResponse;
import com.example.repo_be_v2.domain.user.presentation.dto.response.UserMypageResponse;
import com.example.repo_be_v2.domain.user.presentation.dto.response.ProgressResponse;
import com.example.repo_be_v2.domain.user.presentation.dto.response.ProgressSectionResponse;
import com.example.repo_be_v2.domain.user.service.support.UserReader;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
import com.example.repo_be_v2.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserMypageService {

    private final UserReader userReader;

    /**
     * 마이페이지(홈) 조회
     *
     * 내 정보와 이력서 작성 진행률을 한 번에 돌려준다.
     * 화면이 하나라 API도 하나로 합쳤다.
     */
    @Transactional(readOnly = true)
    public UserMypageResponse execute(Long userId) {
        User user = userReader.getUser(userId);
        Resume resume = userReader.findResume(userId).orElse(null);

        return new UserMypageResponse(
                user.getStudentName(),
                null,
                null,
                resume == null ? null : resume.getIntroduce(),
                user.getMajorName(),
                new ClassInfoResponse(
                        user.getStudentGrade(),
                        user.getStudentClass(),
                        user.getStudentNumber(),
                        userReader.schoolNumberOf(user)
                ),
                toProgress(resume)
        );
    }

    /**
     * 진행률은 이력서 작성 단계로 판정한다.
     *
     * 내 정보   = 이력서를 만들었다
     * 활동      = 내용 있는 페이지를 하나 이상 썼다
     * 프로젝트  = 제출했다
     *
     * 세 칸이 같은 무게라 완료 개수 / 3 을 퍼센트로 만든다. (0, 33, 67, 100)
     */
    private ProgressResponse toProgress(Resume resume) {
        List<ProgressSectionResponse> sections = List.of(
                section(ProgressSection.PROFILE, resume != null),
                section(ProgressSection.ACTIVITY, hasWrittenPage(resume)),
                section(ProgressSection.PROJECT, isSubmitted(resume))
        );

        long completedCount = sections.stream()
                .filter(ProgressSectionResponse::completed)
                .count();

        int totalPercent = (int) Math.round(completedCount * 100.0 / sections.size());

        return new ProgressResponse(totalPercent, sections);
    }

    private ProgressSectionResponse section(ProgressSection key, boolean completed) {
        return new ProgressSectionResponse(key, key.getDisplayName(), completed);
    }

    private boolean hasWrittenPage(Resume resume) {
        if (resume == null || resume.getPages() == null) {
            return false;
        }

        return resume.getPages()
                .stream()
                .map(ResumePage::getContent)
                .anyMatch(content -> content != null && !content.isBlank());
    }

    //제출 이후 상태(SUBMITTED, RELEASED)면 제출한 것으로 본다.
    private boolean isSubmitted(Resume resume) {
        if (resume == null) {
            return false;
        }

        ResumeSubmissionStatus status = resume.getSubmissionStatus();

        return status == ResumeSubmissionStatus.SUBMITTED
                || status == ResumeSubmissionStatus.RELEASED;
    }
}
