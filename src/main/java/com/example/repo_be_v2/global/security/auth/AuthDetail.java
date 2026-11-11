package com.example.repo_be_v2.global.security.auth;

import com.example.repo_be_v2.domain.company.domain.Company;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.enums.Role;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 인증된 계정. 학생·선생님(tbl_user)이거나 기업(tbl_company)이다.
 *
 * accountId는 계정이 들어 있는 테이블의 PK라, 기업과 사용자의 값이 서로 겹칠 수 있다.
 */
public record AuthDetail(
        Long accountId,
        String username,
        String password,
        Role role
) implements UserDetails {

    public static AuthDetail from(User user) {
        return new AuthDetail(user.getId(), user.getStudentEmail(), user.getStudentPassword(), user.getRole());
    }

    //기업은 인증 코드로만 로그인해 이메일도 비밀번호도 없다. 이름 자리에는 회사 이름을 둔다.
    public static AuthDetail from(Company company) {
        return new AuthDetail(company.getId(), company.getName(), null, Role.COMPANY);
    }

    /**
     * 인증된 사용자의 MySQL PK, MongoDB 이력서의 userId와 이어지는 값.
     *
     * 기업의 PK를 사용자 PK로 넘기면 번호가 같은 다른 학생·선생님으로 취급된다.
     * 기업이 닿는 API는 SecurityConfig가 도서관으로 묶어 두지만, 그 설정이 풀리더라도
     * 남의 계정으로 동작하지 않도록 여기서 한 번 더 막는다.
     */
    public Long getId() {
        if (role == Role.COMPANY) {
            throw new AccessDeniedException("기업 계정은 사용자 ID가 필요한 API를 쓸 수 없습니다.");
        }

        return accountId;
    }

    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
