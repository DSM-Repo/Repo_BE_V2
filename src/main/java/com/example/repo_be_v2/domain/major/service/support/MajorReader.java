package com.example.repo_be_v2.domain.major.service.support;

import com.example.repo_be_v2.domain.major.domain.Major;
import com.example.repo_be_v2.domain.major.domain.repository.MajorRepository;
import com.example.repo_be_v2.domain.major.exception.MajorAlreadyExistsException;
import com.example.repo_be_v2.domain.major.exception.MajorInUseException;
import com.example.repo_be_v2.domain.major.exception.MajorNotFoundException;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.enums.Role;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.exception.TeacherPermissionRequiredException;
import com.example.repo_be_v2.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

//전공 서비스들이 공통으로 쓰는 조회와 검증을 모아둔다.
@Component
@RequiredArgsConstructor
public class MajorReader {

    private final MajorRepository majorRepository;
    private final UserRepository userRepository;

    //MySQL에 실제 사용자가 존재하는지 확인
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    //선생님 권한을 가진 사용자인지 확인 (전공 추가·삭제용)
    public User getTeacher(Long userId) {
        User user = getUser(userId);

        if (user.getRole() != Role.TEACHER) {
            throw new TeacherPermissionRequiredException();
        }

        return user;
    }

    public Major getMajor(Long majorId) {
        return majorRepository.findById(majorId)
                .orElseThrow(MajorNotFoundException::new);
    }

    /**
     * 같은 이름의 전공이 이미 있는지 확인한다.
     *
     * DB의 unique 제약이 최종 방어선이지만, 그대로 두면 제약 위반이 500으로 나가므로
     * 여기서 먼저 걸러 409로 내려준다.
     */
    public void validateNameNotDuplicated(String name) {
        if (majorRepository.existsByName(name)) {
            throw new MajorAlreadyExistsException();
        }
    }

    //학생이 쓰고 있는 전공은 지울 수 없다. 몇 명이 쓰는지 함께 알린다.
    public void validateNotInUse(Long majorId) {
        long studentCount = userRepository.countByMajorId(majorId);

        if (studentCount > 0) {
            throw new MajorInUseException(studentCount);
        }
    }
}
