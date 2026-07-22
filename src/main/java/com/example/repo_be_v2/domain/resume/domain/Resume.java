package com.example.repo_be_v2.domain.resume.domain;

import com.example.repo_be_v2.domain.resume.domain.enms.ResumeSubmissionStatus;
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
        this.introduce = introduce;
        this.portfolioUrl = portfolioUrl;
        this.pages = pages;
        this.savedAt = savedAt;
    }

    public void autoSave(List<ResumePage> pages, LocalDateTime savedAt) {
        this.pages = pages;
        this.savedAt = savedAt;
    }

    public void changeVisibility(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void submit(LocalDateTime submittedAt) {
        if (submissionStatus == ResumeSubmissionStatus.SUBMITTED) {
            throw new IllegalStateException("이미 제출된 이력서입니다.");
        }

        if (submissionStatus == ResumeSubmissionStatus.RELEASED) {
            throw new IllegalStateException("이미 공개된 이력서입니다.");
        }

        if (submissionStatus == ResumeSubmissionStatus.DELETED) {
            throw new IllegalStateException("삭제된 이력서는 제출할 수 없습니다.");
        }

        this.submissionStatus = ResumeSubmissionStatus.SUBMITTED;
        this.submittedAt = submittedAt;
    }

    public void release(LocalDateTime releasedAt) {
        if (submissionStatus != ResumeSubmissionStatus.SUBMITTED) {
            throw new IllegalStateException(
                    "제출된 이력서만 공개할 수 있습니다."
            );
        }

        this.submissionStatus = ResumeSubmissionStatus.RELEASED;
        this.isPublic = true;
        this.releasedAt = releasedAt;
    }

    public void delete(LocalDateTime deletedAt) {
        this.submissionStatus = ResumeSubmissionStatus.DELETED;
        this.isPublic = false;
        this.deletedAt = deletedAt;
    }
}
