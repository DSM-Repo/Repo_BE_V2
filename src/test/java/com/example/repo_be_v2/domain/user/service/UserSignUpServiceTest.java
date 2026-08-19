package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.enums.Role;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.presentation.dto.request.UserSignUpRequest;
import com.example.repo_be_v2.domain.user.presentation.dto.response.UserResponse;
import com.example.repo_be_v2.global.exception.UserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSignUpServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserSignUpService userSignUpService;

    @Test
    void 회원가입_시_비밀번호를_암호화하고_사용자를_저장한다() {
        UserSignUpRequest request = new UserSignUpRequest(
                "홍길동", "20260001", "password123", "컴퓨터공학", Role.STUDENT
        );
        when(userRepository.existsByStudentId(request.studentId())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userSignUpService.execute(request);

        assertThat(response.studentId()).isEqualTo("20260001");
        assertThat(response.role()).isEqualTo(Role.STUDENT);
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 이미_존재하는_학번으로는_회원가입할_수_없다() {
        UserSignUpRequest request = new UserSignUpRequest(
                "홍길동", "20260001", "password123", "컴퓨터공학", Role.STUDENT
        );
        when(userRepository.existsByStudentId(request.studentId())).thenReturn(true);

        assertThatThrownBy(() -> userSignUpService.execute(request))
                .isInstanceOf(UserException.class)
                .hasMessage("이미 사용 중인 학번입니다.");
    }
}
