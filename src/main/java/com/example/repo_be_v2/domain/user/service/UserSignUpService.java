package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.exception.EmailAlreadyExistsException;
import com.example.repo_be_v2.domain.user.presentation.dto.request.UserSignUpRequest;
import com.example.repo_be_v2.domain.user.presentation.dto.response.UserResponse;
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
    public UserResponse execute(UserSignUpRequest request) {
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
                .studentMajor(request.studentMajor())
                .role(request.role())
                .build();

        User savedUser = userRepository.save(user);
        userEmailVerifyService.clearVerification(email);
        return UserResponse.from(savedUser);
    }
}
