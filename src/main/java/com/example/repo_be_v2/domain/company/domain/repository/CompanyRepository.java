package com.example.repo_be_v2.domain.company.domain.repository;

import com.example.repo_be_v2.domain.company.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    boolean existsByName(String name);

    boolean existsByCode(String code);

    Optional<Company> findByCode(String code);

    List<Company> findAllByOrderByNameAsc();
}
