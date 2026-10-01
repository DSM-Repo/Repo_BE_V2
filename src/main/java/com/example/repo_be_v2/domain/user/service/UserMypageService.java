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
                user.getProfileImageUrl(),
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
}
