package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.user.presentation.dto.response.ClassInfoResponse;
import com.example.repo_be_v2.domain.user.presentation.dto.response.UserMypageResponse;
import com.example.repo_be_v2.domain.user.service.support.UserReader;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserMypageService {

    private final UserReader userReader;
    private final S3Service s3Service;

    /**
     * 마이페이지(홈) 내 정보 조회
     *
     * 홈 상단 프로필 영역에 들어갈 값만 돌려준다.
     * 이력서 완료율은 GET /user/progress, 알림은 GET /alram으로 따로 받는다.
     */
    @Transactional(readOnly = true)
    public UserMypageResponse execute(Long userId) {
        User user = userReader.getUser(userId);
        Resume resume = userReader.findResume(userId).orElse(null);

        return new UserMypageResponse(
                user.getStudentName(),
                s3Service.toViewUrl(profileImageOf(resume, user)),
                resume == null ? null : resume.getIntroduce(),
                user.getMajorName(),
                new ClassInfoResponse(
                        user.getStudentGrade(),
                        user.getStudentClass(),
                        user.getStudentNumber(),
                        userReader.schoolNumberOf(user)
                )
        );
    }

    /**
     * 홈에 보여줄 프로필 사진.
     *
     * 이력서 첫 장에 올린 사진을 우선 쓰고,
     * 이력서가 없거나 사진을 아직 안 올렸으면 내 정보의 사진으로 대신한다.
     */
    private String profileImageOf(Resume resume, User user) {
        return resume == null
                ? user.getProfileImageUrl()
                : resume.profileImageUrlOr(user.getProfileImageUrl());
    }
}
