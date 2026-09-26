package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeResponse;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.resume.service.support.ResumeReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResumeGetService {

    private final ResumeReader resumeReader;

    /**
     * 내 이력서 조회
     *
     * 저장한 구조를 그대로 돌려준다.
     * 이름, 전공, 프로필 사진은 이력서에 없는 값이라 사용자 정보에서 채워 넣는다.
     */
    @Transactional(readOnly = true)
    public ResumeResponse execute(Long userId, String resumeId) {
        User user = resumeReader.getUser(userId);
        Resume resume = resumeReader.getResumeByIdAndUserId(resumeId, userId);

        return ResumeResponse.of(resume, user);
    }
}
