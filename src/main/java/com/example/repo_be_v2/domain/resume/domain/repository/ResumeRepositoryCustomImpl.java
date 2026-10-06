package com.example.repo_be_v2.domain.resume.domain.repository;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ResumeRepositoryCustomImpl implements ResumeRepositoryCustom {

    //제출할 수 있는 상태. 공개(RELEASED)와 삭제(DELETED)는 빠진다.
    private static final List<String> SUBMITTABLE_STATUSES = List.of(
            ResumeSubmissionStatus.ONGOING.name(),
            ResumeSubmissionStatus.SUBMITTED.name()
    );

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<Resume> replaceIfSubmittable(Resume resume) {
        Query query = Query.query(
                Criteria.where("_id").is(resume.getId())
                        .and("submissionStatus").in(SUBMITTABLE_STATUSES)
        );

        return Optional.ofNullable(
                mongoTemplate.findAndReplace(
                        query,
                        resume,
                        FindAndReplaceOptions.options().returnNew()
                )
        );
    }
}
