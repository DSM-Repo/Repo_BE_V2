package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.user.domain.SchoolYear;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.exception.EmailAlreadyExistsException;
import com.example.repo_be_v2.domain.user.presentation.dto.request.UserSignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserSignUpService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserEmailVerifyService userEmailVerifyService;

    @Transactional
    public void execute(UserSignUpRequest request) {
        String email = request.email();
        if (userRepository.existsByStudentEmail(email)) {
            throw new EmailAlreadyExistsException();
        }
        userEmailVerifyService.validateVerified(email);

        //기수는 가입 시점의 학년도와 학년으로 정해 저장한다. 이후 학년이 올라가도 그대로다.
        int cohort = SchoolYear.cohortOf(
                SchoolYear.of(LocalDateTime.now()),
                request.studentGrade()
        );

        User user = User.builder()
                .studentName(request.studentName())
                .studentEmail(request.email())
                .studentGrade(request.studentGrade())
                .studentClass(request.studentClass())
                .studentNumber(request.studentNumber())
                .cohort(cohort)
                .studentPassword(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        userRepository.save(user);
        userEmailVerifyService.clearVerification(email);
    }
}
