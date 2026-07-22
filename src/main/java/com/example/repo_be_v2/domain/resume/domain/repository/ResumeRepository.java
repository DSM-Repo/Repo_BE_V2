package com.example.repo_be_v2.domain.resume.domain.repository;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ResumeRepository
        extends MongoRepository<Resume, String> {

    Optional<Resume> findByUserId(Long userId);

    Optional<Resume> findByIdAndUserId(
            String id,
            Long userId
    );
}
