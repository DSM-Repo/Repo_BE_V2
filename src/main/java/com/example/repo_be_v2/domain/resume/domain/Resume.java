package com.example.repo_be_v2.domain.resume.domain;

import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
import com.example.repo_be_v2.domain.resume.exception.ResumeNotEditableException;
import com.example.repo_be_v2.domain.resume.exception.ResumeReleasedException;
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

    /**
     * 첫 장 머리말 사진.
     * 제출 후엔 잠기므로 도서관에는 공개 당시 사진이 그대로 남는다.
     */
    private String profileImageUrl;

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
            String profileImageUrl,
            List<ResumePage> pages,
            LocalDateTime savedAt
    ) {
        Resume resume = new Resume();

        resume.userId = userId;
        resume.introduce = introduce;
        resume.email = email;
        resume.skills = nullToEmpty(skills);
        resume.portfolioUrl = portfolioUrl;
        resume.profileImageUrl = profileImageUrl;
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
            String profileImageUrl,
            List<ResumePage> pages,
            LocalDateTime savedAt
    ) {
        assertEditable();

        this.introduce = introduce;
        this.email = email;
        this.skills = nullToEmpty(skills);
        this.portfolioUrl = portfolioUrl;
        this.profileImageUrl = profileImageUrl;
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

    /**
     * 이력서 제출. 본문을 함께 받아 저장과 제출을 한 번에 처리한다.
     *
     * 이미 제출한 이력서도 다시 제출할 수 있다. 그때는 최신 본문으로 덮어쓰고
     * 제출 상태를 그대로 유지한다. 고치려고 제출을 취소했다가 다시 내는 과정에서
     * 중간에 실패하면 미제출로 남아버리기 때문에, 한 번의 요청으로 끝내는 편이 안전하다.
     *
     * 공개된 뒤에는 막는다. 선생님이 확인하고 도서관에 올린 내용이
     * 학생 쪽에서 소리 없이 바뀌면 안 되기 때문이다. 공개 해제는 선생님만 할 수 있다.
     */
    public void submit(
            String introduce,
            String email,
            List<String> skills,
            String portfolioUrl,
            String profileImageUrl,
            List<ResumePage> pages,
            LocalDateTime submittedAt
    ) {
        assertSubmittable();

        this.introduce = introduce;
        this.email = email;
        this.skills = nullToEmpty(skills);
        this.portfolioUrl = portfolioUrl;
        this.profileImageUrl = profileImageUrl;
        this.pages = pages;
        this.savedAt = submittedAt;

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

    /**
     * 이력서에 올린 사진을 우선 쓰고,
     * 아직 올리지 않았으면 내 정보의 프로필 사진으로 대신한다.
     */
    public String profileImageUrlOr(String fallback) {
        return profileImageUrl == null || profileImageUrl.isBlank()
                ? fallback
                : profileImageUrl;
    }

    private void assertEditable() {
        if (submissionStatus != ResumeSubmissionStatus.ONGOING) {
            throw new ResumeNotEditableException();
        }
    }

    //제출은 작성 중이거나 이미 제출한 이력서에만 할 수 있다. 공개됐거나 삭제된 것은 막는다.
    private void assertSubmittable() {
        if (submissionStatus == ResumeSubmissionStatus.RELEASED) {
            throw new ResumeReleasedException();
        }

        if (submissionStatus != ResumeSubmissionStatus.ONGOING
                && submissionStatus != ResumeSubmissionStatus.SUBMITTED) {
            throw new ResumeNotEditableException();
        }
    }

    private static List<String> nullToEmpty(List<String> skills) {
        return skills == null ? List.of() : List.copyOf(skills);
    }
}
