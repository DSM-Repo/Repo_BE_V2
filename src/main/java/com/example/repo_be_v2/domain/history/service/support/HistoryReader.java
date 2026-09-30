package com.example.repo_be_v2.domain.history.service.support;

import com.example.repo_be_v2.domain.history.domain.History;
import com.example.repo_be_v2.domain.history.domain.repository.HistoryRepository;
import com.example.repo_be_v2.domain.history.exception.HistoryNotFoundException;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.enums.Role;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.exception.TeacherPermissionRequiredException;
import com.example.repo_be_v2.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

//히스토리 서비스들이 공통으로 쓰는 조회와 검증을 모아둔다.
@Component
@RequiredArgsConstructor
public class HistoryReader {

    /**
     * 최신 날짜가 위로 온다.
     * 같은 날짜는 나중에 등록한 것이 위로 오도록 _id 역순으로 정한다.
     */
    private static final Sort LATEST_FIRST = Sort.by(
            Sort.Order.desc("date"),
            Sort.Order.desc("_id")
    );

    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;

    //선생님 권한을 가진 사용자인지 확인 (히스토리 등록·수정·삭제용)
    public User getTeacher(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (user.getRole() != Role.TEACHER) {
            throw new TeacherPermissionRequiredException();
        }

        return user;
    }

    public List<History> getHistories() {
        return historyRepository.findAll(LATEST_FIRST);
    }

    public History getHistory(String historyId) {
        return historyRepository.findById(historyId)
                .orElseThrow(HistoryNotFoundException::new);
    }
}
