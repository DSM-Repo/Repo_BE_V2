package com.example.repo_be_v2.domain.feedback.domain.repository;

import com.example.repo_be_v2.domain.feedback.domain.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FeedbackRepository
        extends MongoRepository<Feedback, String> {

    List<Feedback> findAllByResumeIdOrderByCreatedAtAsc(String resumeId);

    List<Feedback> findAllByResumeIdAndPageIdOrderByCreatedAtAsc(String resumeId, String pageId);
}
