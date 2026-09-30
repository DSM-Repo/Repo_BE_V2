package com.example.repo_be_v2.domain.history.domain.repository;

import com.example.repo_be_v2.domain.history.domain.History;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HistoryRepository
        extends MongoRepository<History, String> {
}
