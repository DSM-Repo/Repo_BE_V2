package com.example.repo_be_v2.domain.resume.domain.repository;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ResumeRepositoryCustomImpl implements ResumeRepositoryCustom {

    //학생이 손댈 수 있는 상태. 공개(RELEASED)와 삭제(DELETED)는 빠진다.
    private static final List<String> WRITABLE_STATUSES = List.of(
            ResumeSubmissionStatus.ONGOING.name(),
            ResumeSubmissionStatus.SUBMITTED.name()
    );

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<Resume> updateContentIfWritable(Resume resume) {
        return findAndModify(resume.getId(), contentUpdate(resume));
    }

    @Override
    public Optional<Resume> submitIfWritable(Resume resume) {
        Update update = contentUpdate(resume)
                .set("submissionStatus", resume.getSubmissionStatus())
                .set("submittedAt", resume.getSubmittedAt());

        return findAndModify(resume.getId(), update);
    }

    /**
     * 저장과 제출이 함께 덮어쓰는 본문 필드.
     *
     * 제출 상태와 공개 관련 값은 여기 없다.
     * 저장이 그 값들까지 건드리면 동시에 들어온 제출이나 공개를 되돌리게 된다.
     */
    private Update contentUpdate(Resume resume) {
        return new Update()
                .set("introduce", resume.getIntroduce())
                .set("email", resume.getEmail())
                .set("skills", resume.getSkills())
                .set("portfolioUrl", resume.getPortfolioUrl())
                .set("profileImageUrl", resume.getProfileImageUrl())
                .set("pages", resume.getPages())
                .set("savedAt", resume.getSavedAt());
    }

    private Optional<Resume> findAndModify(String resumeId, Update update) {
        Query query = Query.query(
                Criteria.where("_id").is(resumeId)
                        .and("submissionStatus").in(WRITABLE_STATUSES)
        );

        return Optional.ofNullable(
                mongoTemplate.findAndModify(
                        query,
                        update,
                        FindAndModifyOptions.options().returnNew(true),
                        Resume.class
                )
        );
    }
}
