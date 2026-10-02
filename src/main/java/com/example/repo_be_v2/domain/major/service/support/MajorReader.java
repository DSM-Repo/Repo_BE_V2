package com.example.repo_be_v2.domain.major.service.support;

import com.example.repo_be_v2.domain.major.domain.Major;
import com.example.repo_be_v2.domain.major.domain.repository.MajorRepository;
import com.example.repo_be_v2.domain.major.exception.MajorAlreadyExistsException;
import com.example.repo_be_v2.domain.major.exception.MajorInUseException;
import com.example.repo_be_v2.domain.major.exception.MajorNotFoundException;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeStatusSummary;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.enums.Role;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.domain.user.exception.TeacherPermissionRequiredException;
import com.example.repo_be_v2.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

//전공 서비스들이 공통으로 쓰는 조회와 검증을 모아둔다.
@Component
@RequiredArgsConstructor
public class MajorReader {

    private final MajorRepository majorRepository;
    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

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

    //전공에 소속된 학생 목록. 학년·반이 null이면 그 조건은 걸지 않는다.
    public List<User> getStudentsByMajor(Long majorId, Integer grade, Integer classNumber) {
        return userRepository.findStudentsByMajorId(Role.STUDENT, majorId, grade, classNumber);
    }

    /**
     * 학생별 이력서 상태를 userId로 찾을 수 있게 묶는다.
     *
     * 학생은 MySQL, 이력서는 MongoDB에 있어 한 쿼리로 묶을 수 없다.
     * 이력서는 학생당 하나(userId unique)라 Map으로 바로 만들 수 있고,
     * 아직 이력서를 만들지 않은 학생은 Map에 없어 호출부가 미제출로 취급한다.
     */
    public Map<Long, ResumeStatusSummary> getResumeStatusByUserId(Collection<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        return resumeRepository.findByUserIdIn(userIds)
                .stream()
                .collect(Collectors.toMap(ResumeStatusSummary::getUserId, Function.identity()));
    }

    //학번은 학년·반·번호를 이어 붙인다. (1학년 3반 5번 -> "1305")
    public String schoolNumberOf(User user) {
        return "%d%d%02d".formatted(
                user.getStudentGrade(),
                user.getStudentClass(),
                user.getStudentNumber()
        );
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
