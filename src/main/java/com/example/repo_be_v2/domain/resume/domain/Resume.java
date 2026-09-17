package com.example.repo_be_v2.domain.resume.domain;

import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
import com.example.repo_be_v2.domain.resume.exception.ResumeNotEditableException;
import com.example.repo_be_v2.domain.resume.exception.ResumeNotSubmittedException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "resumes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Resume {

    @Id
    private String id;

    @Indexed(unique = true)
    private Long userId;

    private String introduce;

    /**
     * 첫 장 머리말에 적는 연락용 이메일.
     * 로그인 계정(User.studentEmail)과는 별개로 학생이 직접 적는 값이다.
     */
    private String email;

    private List<String> skills;

    private String portfolioUrl;

    private boolean isPublic;

    private ResumeSubmissionStatus submissionStatus;

    private List<ResumePage> pages;

    private LocalDateTime savedAt;

    private LocalDateTime submittedAt;

    private LocalDateTime releasedAt;

    private LocalDateTime deletedAt;

    public static Resume create(
            Long userId,
            String introduce,
            String email,
            List<String> skills,
            String portfolioUrl,
            List<ResumePage> pages,
            LocalDateTime savedAt
    ) {
        Resume resume = new Resume();

        resume.userId = userId;
        resume.introduce = introduce;
        resume.email = email;
        resume.skills = nullToEmpty(skills);
        resume.portfolioUrl = portfolioUrl;
        resume.isPublic = false;
        resume.submissionStatus = ResumeSubmissionStatus.ONGOING;
        resume.pages = pages;
        resume.savedAt = savedAt;

        return resume;
    }

    /**
     * 이력서 본문 저장.
     *
     * 수동 저장과 자동 저장이 같은 요청을 쓰므로 저장 경로도 하나다.
     * 자동 저장이 머리말만 빼고 덮어쓰면 기술스택이나 이메일이 되돌아가기 때문이다.
     */
    public void save(
            String introduce,
            String email,
            List<String> skills,
            String portfolioUrl,
            List<ResumePage> pages,
            LocalDateTime savedAt
    ) {
        assertEditable();

        this.introduce = introduce;
        this.email = email;
        this.skills = nullToEmpty(skills);
        this.portfolioUrl = portfolioUrl;
        this.pages = pages;
        this.savedAt = savedAt;
    }

    public void changeVisibility(boolean isPublic, LocalDateTime now) {
        if (isPublic) {
            release(now);
        } else {
            unrelease();
        }
    }

    public void submit(LocalDateTime submittedAt) {
        assertEditable();

        this.submissionStatus = ResumeSubmissionStatus.SUBMITTED;
        this.submittedAt = submittedAt;
    }

    public void cancelSubmit() {
        if (submissionStatus != ResumeSubmissionStatus.SUBMITTED) {
            throw new ResumeNotSubmittedException();
        }

        this.submissionStatus = ResumeSubmissionStatus.ONGOING;
        this.submittedAt = null;
    }

    public void release(LocalDateTime releasedAt) {
        if (submissionStatus != ResumeSubmissionStatus.SUBMITTED
                && submissionStatus != ResumeSubmissionStatus.RELEASED) {
            throw new ResumeNotSubmittedException();
        }

        this.submissionStatus = ResumeSubmissionStatus.RELEASED;
        this.isPublic = true;
        this.releasedAt = releasedAt;
    }

    public void unrelease() {
        if (submissionStatus == ResumeSubmissionStatus.RELEASED) {
            this.submissionStatus = ResumeSubmissionStatus.SUBMITTED;
            this.releasedAt = null;
        }

        this.isPublic = false;
    }

    public void delete(LocalDateTime deletedAt) {
        this.submissionStatus = ResumeSubmissionStatus.DELETED;
        this.isPublic = false;
        this.deletedAt = deletedAt;
    }

    public List<String> getSkills() {
        return nullToEmpty(skills);
    }

    private void assertEditable() {
        if (submissionStatus != ResumeSubmissionStatus.ONGOING) {
            throw new ResumeNotEditableException();
        }
    }

    private static List<String> nullToEmpty(List<String> skills) {
        return skills == null ? List.of() : List.copyOf(skills);
    }
}
