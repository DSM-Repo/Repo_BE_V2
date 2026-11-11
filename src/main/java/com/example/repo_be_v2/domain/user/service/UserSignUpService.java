package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.domain.enums.Role;
import com.example.repo_be_v2.domain.user.exception.EmailAlreadyExistsException;
import com.example.repo_be_v2.domain.user.exception.SignUpRoleNotAllowedException;
import com.example.repo_be_v2.domain.user.presentation.dto.request.UserSignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSignUpService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEmailVerifyService userEmailVerifyService;

    @Transactional
    public void execute(UserSignUpRequest request) {
        //기업 계정은 선생님만 발급한다. 가입 요청에 role을 COMPANY로 넣어 만드는 길을 막는다.
        if (request.role() == Role.COMPANY) {
            throw new SignUpRoleNotAllowedException();
        }

        String email = request.email();
        if (userRepository.existsByStudentEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
        userEmailVerifyService.validateVerified(email);

        User user = User.builder()
                .studentName(request.studentName())
                .studentEmail(request.email())
                .studentGrade(request.studentGrade())
                .studentClass(request.studentClass())
                .studentNumber(request.studentNumber())
                .studentPassword(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        userRepository.save(user);
        userEmailVerifyService.clearVerification(email);
    }
}
