package com.example.repo_be_v2.domain.company.service.support;

import com.example.repo_be_v2.domain.company.domain.Company;
import com.example.repo_be_v2.domain.company.domain.CompanyCode;
import com.example.repo_be_v2.domain.company.domain.repository.CompanyRepository;
import com.example.repo_be_v2.domain.company.exception.CompanyAlreadyExistsException;
import com.example.repo_be_v2.domain.company.exception.CompanyNotFoundException;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.enums.Role;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.exception.TeacherPermissionRequiredException;
import com.example.repo_be_v2.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

//기업 계정 서비스들이 공통으로 쓰는 조회와 검증을 모아둔다.
@Component
@RequiredArgsConstructor
public class CompanyReader {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    //선생님 권한을 가진 사용자인지 확인 (기업 계정 발급·조회·삭제용)
    public User getTeacher(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (user.getRole() != Role.TEACHER) {
            throw new TeacherPermissionRequiredException();
        }

        return user;
    }

    public Company getCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(CompanyNotFoundException::new);
    }

    /**
     * 같은 이름의 기업 계정이 이미 있는지 확인한다.
     *
     * DB의 unique 제약이 최종 방어선이지만, 어떤 값이 겹쳤는지 알려주려고 먼저 거른다.
     */
    public void validateNameNotDuplicated(String name) {
        if (companyRepository.existsByName(name)) {
            throw new CompanyAlreadyExistsException();
        }
    }

    /**
     * 아직 아무도 쓰지 않는 인증 코드를 만든다.
     *
     * 경우의 수가 2^80이라 겹칠 일은 사실상 없지만, 겹치면 저장이 unique 제약에 걸려 발급이 실패하므로 확인하고 낸다.
     */
    public String newCode() {
        String code = CompanyCode.generate();

        while (companyRepository.existsByCode(code)) {
            code = CompanyCode.generate();
        }

        return code;
    }
}
