package com.example.repo_be_v2.domain.resume.service.support;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.ResumeProject;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumePageType;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeStatusSummary;
import com.example.repo_be_v2.domain.resume.exception.ResumeNotFoundException;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumePageRequest;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumeProjectRequest;
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
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

//이력서 서비스들이 공통으로 쓰는 조회와 변환을 모아둔다.
@Component
@RequiredArgsConstructor
public class ResumeReader {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    //MySQL에 실제 사용자가 존재하는지 확인
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    //선생님 권한을 가진 사용자인지 확인 (학생 제출 현황 조회용)
    public User getTeacher(Long userId) {
        User user = getUser(userId);

        if (user.getRole() != Role.TEACHER) {
            throw new TeacherPermissionRequiredException();
        }

        return user;
    }

    //학생 목록. 학년·반이 null이면 그 조건은 걸지 않는다.
    public List<User> getStudents(Integer grade, Integer classNumber) {
        return userRepository.findStudents(Role.STUDENT, grade, classNumber);
    }

    /**
     * 학생별 이력서 상태를 userId로 찾을 수 있게 묶는다.
     *
     * 이력서는 학생당 하나(userId unique)라 Map으로 바로 만들 수 있다.
     * 아직 이력서를 만들지 않은 학생은 Map에 없고, 호출부가 미제출로 취급한다.
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

    //유저 소유의 이력서를 id 기준으로 조회
    public Resume getResumeByIdAndUserId(String resumeId, Long userId) {
        return resumeRepository.findByIdAndUserId(resumeId, userId)
                .orElseThrow(ResumeNotFoundException::new);
    }

    //유저 소유의 이력서를 조회
    public Resume getResumeByUserId(Long userId) {
        return resumeRepository.findByUserId(userId)
                .orElseThrow(ResumeNotFoundException::new);
    }

    /**
     * 요청 DTO의 페이지 목록을 도메인 객체로 변환한다.
     *
     * 페이지 id는 피드백이 물고 있는 값이라 저장할 때마다 새로 만들면 안 된다.
     * 요청이 id를 들고 오면 그대로 쓰고,
     * 비워서 보내면 같은 자리에 있던 기존 페이지의 id를 물려받는다.
     * 둘 다 없을 때(새 페이지, id 없이 저장된 옛 데이터)만 새로 발급한다.
     */
    public List<ResumePage> toResumePages(Resume resume, List<ResumePageRequest> pageRequests) {
        List<ResumePage> previousPages = resume == null ? List.of() : resume.getPages();

        return pageRequests.stream()
                .map(page -> new ResumePage(
                        resolvePageId(previousPages, page),
                        page.index(),
                        page.type(),
                        toResumeProject(page),
                        page.content()
                ))
                .toList();
    }

    //머리말은 프로젝트 페이지에만 있다. 다른 종류로 실려오면 버린다.
    private ResumeProject toResumeProject(ResumePageRequest page) {
        if (page.type() != ResumePageType.PROJECT || page.project() == null) {
            return null;
        }

        ResumeProjectRequest project = page.project();

        return ResumeProject.of(
                project.name(),
                project.imageUrl(),
                project.summary(),
                project.startDate(),
                project.endDate()
        );
    }

    private String resolvePageId(List<ResumePage> previousPages, ResumePageRequest request) {
        if (request.id() != null && !request.id().isBlank()) {
            return request.id();
        }

        return findPreviousPageId(previousPages, request.index())
                .orElseGet(() -> UUID.randomUUID().toString());
    }

    private Optional<String> findPreviousPageId(List<ResumePage> previousPages, int index) {
        if (previousPages == null) {
            return Optional.empty();
        }

        return previousPages.stream()
                .filter(page -> page.getIndex() == index && page.getId() != null)
                .map(ResumePage::getId)
                .findFirst();
    }
}
