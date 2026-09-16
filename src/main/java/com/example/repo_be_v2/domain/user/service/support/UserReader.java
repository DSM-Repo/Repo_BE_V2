package com.example.repo_be_v2.domain.user.service.support;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

//마이페이지가 쓰는 조회를 모아둔다.
@Component
@RequiredArgsConstructor
public class UserReader {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    //이력서는 아직 없을 수 있다. 없으면 진행률 0%로 보여주면 되므로 예외를 던지지 않는다.
    public Optional<Resume> findResume(Long userId) {
        return resumeRepository.findByUserId(userId);
    }

    //학번은 학년·반·번호를 이어 붙인다. (2학년 3반 5번 -> "2305")
    public String schoolNumberOf(User user) {
        return "%d%d%02d".formatted(
                user.getStudentGrade(),
                user.getStudentClass(),
                user.getStudentNumber()
        );
    }
}
