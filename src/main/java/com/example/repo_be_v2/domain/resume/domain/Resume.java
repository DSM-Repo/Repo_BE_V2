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
            String portfolioUrl,
            List<ResumePage> pages,
            LocalDateTime savedAt
    ) {
        Resume resume = new Resume();

        resume.userId = userId;
        resume.introduce = introduce;
        resume.portfolioUrl = portfolioUrl;
        resume.isPublic = false;
        resume.submissionStatus = ResumeSubmissionStatus.ONGOING;
        resume.pages = pages;
        resume.savedAt = savedAt;

        return resume;
    }

    public void save(
            String introduce,
            String portfolioUrl,
            List<ResumePage> pages,
            LocalDateTime savedAt
    ) {
        assertEditable();

        this.introduce = introduce;
        this.portfolioUrl = portfolioUrl;
        this.pages = pages;
        this.savedAt = savedAt;
    }

    public void autoSave(List<ResumePage> pages, LocalDateTime savedAt) {
        assertEditable();

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

    private void assertEditable() {
        if (submissionStatus != ResumeSubmissionStatus.ONGOING) {
            throw new ResumeNotEditableException();
        }
    }
}
