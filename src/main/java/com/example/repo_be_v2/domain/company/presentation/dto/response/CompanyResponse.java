package com.example.repo_be_v2.domain.company.presentation.dto.response;

import com.example.repo_be_v2.domain.company.domain.Company;

import java.time.LocalDateTime;

//비밀번호는 발급한 선생님이 직접 전달하는 값이라 응답에 담지 않는다.
public record CompanyResponse(
        Long companyId,
        String name,
        String email,
        LocalDateTime createdAt
) {

    public static CompanyResponse from(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getEmail(),
                company.getCreatedAt()
        );
    }
}
