package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.user.presentation.dto.response.ClassInfoResponse;
import com.example.repo_be_v2.domain.user.presentation.dto.response.UserMypageResponse;
import com.example.repo_be_v2.domain.user.service.support.ResumeProgressCalculator;
import com.example.repo_be_v2.domain.user.service.support.UserReader;
import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.notification.service.NotificationListService;
import com.example.repo_be_v2.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserMypageService {

    private final UserReader userReader;
    private final ResumeProgressCalculator resumeProgressCalculator;
    private final NotificationListService notificationListService;

    /**
     * 마이페이지(홈) 조회
     *
     * 내 정보와 이력서 작성 진행률, 알림 목록을 한 번에 돌려준다.
     * 화면이 하나라 API도 하나로 합쳤다.
     */
    @Transactional(readOnly = true)
    public UserMypageResponse execute(Long userId) {
        User user = userReader.getUser(userId);
        Resume resume = userReader.findResume(userId).orElse(null);

        return new UserMypageResponse(
                user.getStudentName(),
                profileImageOf(resume, user),
                resume == null ? null : resume.getIntroduce(),
                user.getMajorName(),
                new ClassInfoResponse(
                        user.getStudentGrade(),
                        user.getStudentClass(),
                        user.getStudentNumber(),
                        userReader.schoolNumberOf(user)
                ),
                resumeProgressCalculator.execute(user, resume),
                notificationListService.execute(userId)
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
