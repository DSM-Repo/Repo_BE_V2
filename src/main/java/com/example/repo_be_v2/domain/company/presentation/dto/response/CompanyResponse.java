package com.example.repo_be_v2.domain.company.presentation.dto.response;

import com.example.repo_be_v2.domain.company.domain.Company;
import com.example.repo_be_v2.domain.company.domain.CompanyCode;

import java.time.LocalDateTime;

//code는 선생님이 기업에 전달할 인증 코드다. 네 글자씩 끊은 모양으로 내려준다.
public record CompanyResponse(
        Long companyId,
        String name,
        String code,
        LocalDateTime createdAt
) {

    public static CompanyResponse from(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                CompanyCode.format(company.getCode()),
                company.getCreatedAt()
        );
    }
}
