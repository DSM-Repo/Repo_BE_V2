package com.example.repo_be_v2.domain.user.domain.repository;

import com.example.repo_be_v2.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
