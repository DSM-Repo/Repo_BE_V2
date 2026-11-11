package com.example.repo_be_v2.domain.company.domain.repository;

import com.example.repo_be_v2.domain.company.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsByEmail(String email);

    Optional<Company> findByEmail(String email);

    List<Company> findAllByOrderByNameAsc();
}
