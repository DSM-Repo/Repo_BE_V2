package com.example.repo_be_v2.global.security.auth;

import com.example.repo_be_v2.domain.user.domain.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record AuthDetail(User user) implements UserDetails {
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
<<<<<<< HEAD
        return user.getStudentEmail();
=======
        return user.getStudentId();
>>>>>>> origin/1-feat-user-login
    }
}
