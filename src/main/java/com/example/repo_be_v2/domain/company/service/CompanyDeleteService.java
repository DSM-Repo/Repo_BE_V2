package com.example.repo_be_v2.domain.company.service;

import com.example.repo_be_v2.domain.company.domain.Company;
import com.example.repo_be_v2.domain.company.domain.repository.CompanyRepository;
import com.example.repo_be_v2.domain.company.service.support.CompanyReader;
import com.example.repo_be_v2.domain.user.service.UserRefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyDeleteService {

    private final CompanyRepository companyRepository;
    private final CompanyReader companyReader;
    private final UserRefreshTokenService refreshTokenService;

    /**
     * 기업 계정 삭제 (선생님 권한)
     *
     * 계정에 기한을 두지 않는 대신 삭제가 곧 접근 차단이다.
     * 인증 필터가 요청마다 계정을 다시 찾으므로 이미 발급된 액세스 토큰도 삭제 즉시 막히고,
     * 리프레시 토큰도 함께 지워 재발급을 못 하게 한다.
     */
    @Transactional
    public void execute(Long teacherId, Long companyId) {
        companyReader.getTeacher(teacherId);

        Company company = companyReader.getCompany(companyId);

        companyRepository.delete(company);
        refreshTokenService.deleteForCompany(company.getId());
    }
}
