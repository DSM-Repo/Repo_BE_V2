package com.example.repo_be_v2.domain.company.service;

import com.example.repo_be_v2.domain.company.domain.Company;
import com.example.repo_be_v2.domain.company.domain.repository.CompanyRepository;
import com.example.repo_be_v2.domain.company.service.support.CompanyReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyDeleteService {

    private final CompanyRepository companyRepository;
    private final CompanyReader companyReader;

    /**
     * 기업 계정 삭제 (선생님 권한)
     *
     * 코드에 기한을 두지 않는 대신 삭제가 곧 접근 차단이다.
     * 인증 필터와 토큰 재발급이 요청마다 계정을 다시 찾으므로,
     * 삭제하면 코드는 물론 그 코드로 이미 받아 간 토큰도 바로 쓸 수 없게 된다.
     */
    @Transactional
    public void execute(Long teacherId, Long companyId) {
        companyReader.getTeacher(teacherId);

        Company company = companyReader.getCompany(companyId);

        companyRepository.delete(company);
    }
}
