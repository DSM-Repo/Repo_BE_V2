package com.example.repo_be_v2.domain.user.domain.repository;

import com.example.repo_be_v2.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByStudentId(String studentId);

    java.util.Optional<User> findByStudentId(String studentId);
}
