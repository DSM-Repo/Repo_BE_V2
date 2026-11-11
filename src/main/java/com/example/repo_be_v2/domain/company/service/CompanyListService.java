package com.example.repo_be_v2.domain.company.service;

import com.example.repo_be_v2.domain.company.domain.repository.CompanyRepository;
import com.example.repo_be_v2.domain.company.presentation.dto.response.CompanyListResponse;
import com.example.repo_be_v2.domain.company.presentation.dto.response.CompanyResponse;
import com.example.repo_be_v2.domain.company.service.support.CompanyReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyListService {

    private final CompanyRepository companyRepository;
    private final CompanyReader companyReader;

    //기업 계정 목록 조회 (선생님 권한). 회사 이름 오름차순으로 내려준다.
    @Transactional(readOnly = true)
    public CompanyListResponse execute(Long teacherId) {
        companyReader.getTeacher(teacherId);

        List<CompanyResponse> companies = companyRepository.findAllByOrderByNameAsc()
                .stream()
                .map(CompanyResponse::from)
                .toList();

        return new CompanyListResponse(companies, companies.size());
    }
}
