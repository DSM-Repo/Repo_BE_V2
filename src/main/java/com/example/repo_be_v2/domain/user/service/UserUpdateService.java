package com.example.repo_be_v2.domain.user.service;

import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.presentation.dto.request.UserUpdateRequest;
import com.example.repo_be_v2.domain.user.service.support.UserReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserUpdateService {

    private final UserReader userReader;

    /**
     * 내 정보 수정 (홈 화면 프로필 영역)
     *
     * 요청에 담긴 필드만 바꾼다. null인 필드는 "수정 안 함"이다.
     * 전공은 선생님이 만들어둔 카탈로그에 있는 것만 고를 수 있다.
     */
    @Transactional
    public void execute(Long userId, UserUpdateRequest request) {
        User user = userReader.getUser(userId);

        if (request.majorId() != null) {
            user.changeMajor(userReader.getMajor(request.majorId()));
        }

        if (request.profileImageUrl() != null) {
            user.changeProfileImage(request.profileImageUrl());
        }
    }
}
