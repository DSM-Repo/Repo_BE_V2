package com.example.repo_be_v2.domain.user.domain.repository;

import com.example.repo_be_v2.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByStudentEmail(String studentEmail);

    Optional<User> findByStudentEmail(String studentEmail);

    //전공 삭제 전 사용 중인지 판정한다.
    long countByMajorId(Long majorId);
}
