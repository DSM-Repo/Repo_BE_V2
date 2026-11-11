package com.example.repo_be_v2.domain.company.service;

import com.example.repo_be_v2.domain.company.domain.Company;
import com.example.repo_be_v2.domain.company.domain.repository.CompanyRepository;
import com.example.repo_be_v2.domain.company.presentation.dto.request.CompanyCreateRequest;
import com.example.repo_be_v2.domain.company.presentation.dto.response.CompanyResponse;
import com.example.repo_be_v2.domain.company.service.support.CompanyReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyCreateService {

    private final CompanyRepository companyRepository;
    private final CompanyReader companyReader;

    /**
     * 기업 계정 발급 (선생님 권한)
     *
     * 선생님이 회사 이름을 넣으면 인증 코드를 만들어 돌려준다.
     * 선생님은 이 코드를 회사에 전달하고, 기업은 코드만 입력해 로그인한다.
     *
     * 이름 양끝 공백은 서버가 정리한다.
     * "토스"와 "토스 "가 다른 회사로 등록되면 중복 판정이 무너지기 때문이다.
     */
    @Transactional
    public CompanyResponse execute(Long teacherId, CompanyCreateRequest request) {
        companyReader.getTeacher(teacherId);

        String name = request.name().trim();
        companyReader.validateNameNotDuplicated(name);

        Company company = companyRepository.save(Company.create(name, companyReader.newCode()));

        return CompanyResponse.from(company);
    }
}
