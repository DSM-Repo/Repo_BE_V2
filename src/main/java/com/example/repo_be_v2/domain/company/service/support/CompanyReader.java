package com.example.repo_be_v2.domain.company.service.support;

import com.example.repo_be_v2.domain.company.domain.Company;
import com.example.repo_be_v2.domain.company.domain.repository.CompanyRepository;
import com.example.repo_be_v2.domain.company.exception.CompanyEmailAlreadyExistsException;
import com.example.repo_be_v2.domain.company.exception.CompanyEmailNotAllowedException;
import com.example.repo_be_v2.domain.company.exception.CompanyNotFoundException;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.enums.Role;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.exception.TeacherPermissionRequiredException;
import com.example.repo_be_v2.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;

//기업 계정 서비스들이 공통으로 쓰는 조회와 검증을 모아둔다.
@Component
@RequiredArgsConstructor
public class CompanyReader {

    private static final String SCHOOL_EMAIL_DOMAIN = "@dsm.hs.kr";

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
     * 학교 이메일은 기업 계정에 쓸 수 없다.
     *
     * 로그인은 학생·선생님과 같은 API를 쓰고 이메일로 계정을 찾는다.
     * 학생·선생님은 학교 이메일로만 가입하므로, 여기서 학교 이메일을 막아 두면
     * 두 테이블의 이메일이 겹칠 수 없어 어느 계정인지 헷갈릴 일이 없다.
     */
    public void validateNotSchoolEmail(String email) {
        if (email.toLowerCase(Locale.ROOT).endsWith(SCHOOL_EMAIL_DOMAIN)) {
            throw new CompanyEmailNotAllowedException();
        }
    }

    /**
     * 같은 이메일의 기업 계정이 이미 있는지 확인한다.
     *
     * DB의 unique 제약이 최종 방어선이지만, 어떤 값이 겹쳤는지 알려주려고 먼저 거른다.
     */
    public void validateEmailNotDuplicated(String email) {
        if (companyRepository.existsByEmail(email)) {
            throw new CompanyEmailAlreadyExistsException();
        }
    }
}
