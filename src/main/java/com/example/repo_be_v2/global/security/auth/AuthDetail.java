package com.example.repo_be_v2.global.security.auth;

import com.example.repo_be_v2.domain.user.domain.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record AuthDetail(User user) implements UserDetails {

    // 인증된 사용자의 MySQL PK, MongoDB 이력서의 userId와 이어지는 값
    public Long getId() {
        return user.getId();
    }

    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getStudentPassword();
    }

    @Override
    public String getUsername() {
        return user.getStudentEmail();
    }
}
