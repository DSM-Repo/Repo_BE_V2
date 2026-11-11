package com.example.repo_be_v2.domain.company.service;

import com.example.repo_be_v2.domain.company.domain.Company;
import com.example.repo_be_v2.domain.company.domain.repository.CompanyRepository;
import com.example.repo_be_v2.domain.company.presentation.dto.request.CompanyCreateRequest;
import com.example.repo_be_v2.domain.company.presentation.dto.response.CompanyResponse;
import com.example.repo_be_v2.domain.company.service.support.CompanyReader;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyCreateService {

    private final CompanyRepository companyRepository;
    private final CompanyReader companyReader;
    private final PasswordEncoder passwordEncoder;

    /**
     * 기업 계정 발급 (선생님 권한)
     *
     * 기업은 외부인이라 스스로 가입하지 못하고, 선생님이 회사마다 계정을 만들어 전달한다.
     * 선생님이 정한 이메일을 그대로 믿으므로 이메일 인증은 거치지 않는다.
     */
    @Transactional
    public CompanyResponse execute(Long teacherId, CompanyCreateRequest request) {
        companyReader.getTeacher(teacherId);

        String email = request.email().trim();
        companyReader.validateNotSchoolEmail(email);
        companyReader.validateEmailNotDuplicated(email);

        Company company = companyRepository.save(Company.create(
                request.name().trim(),
                email,
                passwordEncoder.encode(request.password())
        ));

        return CompanyResponse.from(company);
    }
}
