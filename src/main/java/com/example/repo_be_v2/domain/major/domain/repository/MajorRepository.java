package com.example.repo_be_v2.domain.major.domain.repository;

import com.example.repo_be_v2.domain.major.domain.Major;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MajorRepository extends JpaRepository<Major, Long> {

    boolean existsByName(String name);

    List<Major> findAllByOrderByNameAsc();
}
