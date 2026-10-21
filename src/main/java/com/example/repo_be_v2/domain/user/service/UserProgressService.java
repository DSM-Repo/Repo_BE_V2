package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.presentation.dto.response.ProgressResponse;
import com.example.repo_be_v2.domain.user.service.support.ResumeProgressCalculator;
import com.example.repo_be_v2.domain.user.service.support.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProgressService {

    private final UserReader userReader;
    private final ResumeProgressCalculator resumeProgressCalculator;

    /**
     * 이력서 완료율 조회
     *
     * 홈 화면 도넛과 칸별 퍼센트에 쓰인다.
     * 이력서를 아직 만들지 않았으면 모든 칸이 0%다.
     */
    @Transactional(readOnly = true)
    public ProgressResponse execute(Long userId) {
        User user = userReader.getUser(userId);
        Resume resume = userReader.findResume(userId).orElse(null);

        return resumeProgressCalculator.execute(user, resume);
    }
}
