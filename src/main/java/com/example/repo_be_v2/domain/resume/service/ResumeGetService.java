package com.example.repo_be_v2.domain.resume.service;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.presentation.dto.response.ResumeResponse;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.resume.service.support.ResumeReader;
import com.example.repo_be_v2.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResumeGetService {

    private final ResumeReader resumeReader;
    private final S3Service s3Service;

    /**
     * 내 이력서 조회
     *
     * 이력서는 학생당 하나(userId unique)라 토큰의 사용자만으로 찾는다.
     * 화면이 이력서 id를 따로 들고 있지 않아도 되므로, 기기·브라우저가 바뀌어도 조회된다.
     *
     * 저장한 구조를 그대로 돌려준다.
     * 이름, 전공, 프로필 사진은 이력서에 없는 값이라 사용자 정보에서 채워 넣는다.
     */
    @Transactional(readOnly = true)
    public ResumeResponse execute(Long userId) {
        User user = resumeReader.getUser(userId);
        Resume resume = resumeReader.getResumeByUserId(userId);

        return ResumeResponse.of(resume, user, s3Service::toViewUrl);
    }

    //이력서 id로 찾는 옛 방식. 화면이 execute(userId)로 옮겨 가면 지운다.
    @Transactional(readOnly = true)
    public ResumeResponse execute(Long userId, String resumeId) {
        User user = resumeReader.getUser(userId);
        Resume resume = resumeReader.getResumeByIdAndUserId(resumeId, userId);

        return ResumeResponse.of(resume, user, s3Service::toViewUrl);
    }
}
