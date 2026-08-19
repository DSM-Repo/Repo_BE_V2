package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.enums.Role;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.presentation.dto.request.UserLoginRequest;
import com.example.repo_be_v2.domain.user.presentation.dto.response.TokenResponse;
import com.example.repo_be_v2.global.exception.UserException;
import com.example.repo_be_v2.global.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private UserLoginService userLoginService;

    @Test
    void 올바른_학번과_비밀번호로_로그인하면_토큰을_반환한다() {
        User user = createUser();
        UserLoginRequest request = new UserLoginRequest("20260001", "password123");
        TokenResponse expected = new TokenResponse("Bearer", "access-token", 3600);
        when(userRepository.findByStudentId(request.studentId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getStudentPassword())).thenReturn(true);
        when(jwtTokenProvider.createToken(user.getStudentId(), user.getRole())).thenReturn(expected);

        TokenResponse response = userLoginService.execute(request);

        assertThat(response).isEqualTo(expected);
        verify(jwtTokenProvider).createToken("20260001", Role.STUDENT);
    }

    @Test
    void 비밀번호가_틀리면_로그인할_수_없다() {
        User user = createUser();
        UserLoginRequest request = new UserLoginRequest("20260001", "wrong-password");
        when(userRepository.findByStudentId(request.studentId())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getStudentPassword())).thenReturn(false);

        assertThatThrownBy(() -> userLoginService.execute(request))
                .isInstanceOf(UserException.class)
                .hasMessage("학번 또는 비밀번호가 올바르지 않습니다.");
    }

    private User createUser() {
        return User.builder()
                .studentName("홍길동")
                .studentId("20260001")
                .studentPassword("encoded-password")
                .studentMajor("컴퓨터공학")
                .role(Role.STUDENT)
                .build();
    }
}
