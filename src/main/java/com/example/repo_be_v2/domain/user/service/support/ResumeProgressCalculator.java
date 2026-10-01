package com.example.repo_be_v2.domain.user.service.support;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.ResumeProject;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
import com.example.repo_be_v2.domain.user.domain.User;
import com.example.repo_be_v2.domain.user.domain.enums.ProgressSection;
import com.example.repo_be_v2.domain.user.presentation.dto.response.ProgressResponse;
import com.example.repo_be_v2.domain.user.presentation.dto.response.ProgressSectionResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 이력서 완성도 계산.
 *
 * 칸마다 안에 든 항목을 하나씩 세어 비율을 내고, 비중을 곱해 가운데 퍼센트를 만든다.
 * 한 칸을 통째로 채워야 33%가 오르던 것을 항목 단위로 쪼갠 것이다.
 *
 * 칸별 퍼센트도 함께 내려주므로 도넛을 부분 채움으로 그릴 수 있다.
 * 가운데 퍼센트는 반올림 전 비율로 계산하므로, 칸별 퍼센트를 눈으로 더한 값과
 * 1 정도 어긋날 수 있다. 반올림은 각자 마지막에 한 번씩만 한다.
 */
@Component
public class ResumeProgressCalculator {

    //활동 본문은 이만큼 쓰면 다 쓴 것으로 본다.
    private static final int ACTIVITY_TARGET_LENGTH = 500;

    //프로젝트는 이만큼 쓰면 다 쓴 것으로 본다.
    private static final int PROJECT_TARGET_COUNT = 2;

    private static final int TOTAL_WEIGHT = Arrays.stream(ProgressSection.values())
            .mapToInt(ProgressSection::getWeight)
            .sum();

    public ProgressResponse execute(User user, Resume resume) {
        Map<ProgressSection, Double> ratios = ratios(user, resume);

        List<ProgressSectionResponse> sections = Arrays.stream(ProgressSection.values())
                .map(section -> toSection(section, ratios.get(section)))
                .toList();

        return new ProgressResponse(totalPercent(ratios), sections);
    }

    private ProgressSectionResponse toSection(ProgressSection section, double ratio) {
        return new ProgressSectionResponse(
                section,
                section.getDisplayName(),
                percent(ratio),
                ratio >= 1.0
        );
    }

    private Map<ProgressSection, Double> ratios(User user, Resume resume) {
        Map<ProgressSection, Double> ratios = new EnumMap<>(ProgressSection.class);

        if (resume == null) {
            //이력서를 아직 만들지 않았으면 셀 것이 없다.
            Arrays.stream(ProgressSection.values()).forEach(section -> ratios.put(section, 0.0));

            return ratios;
        }

        if (isSubmitted(resume)) {
            //제출했다면 더 쓸 것이 없으므로 항목을 세지 않고 다 채운 것으로 본다.
            Arrays.stream(ProgressSection.values()).forEach(section -> ratios.put(section, 1.0));

            return ratios;
        }

        ratios.put(ProgressSection.PROFILE, profileRatio(user, resume));
        ratios.put(ProgressSection.ACTIVITY, activityRatio(resume));
        ratios.put(ProgressSection.PROJECT, projectRatio(resume));

        return ratios;
    }

    private int totalPercent(Map<ProgressSection, Double> ratios) {
        double weighted = ratios.entrySet()
                .stream()
                .mapToDouble(entry -> entry.getValue() * entry.getKey().getWeight())
                .sum();

        return (int) Math.round(weighted / TOTAL_WEIGHT * 100);
    }

    private int percent(double ratio) {
        return (int) Math.round(ratio * 100);
    }

    //머리말에 들어가는 값들을 항목으로 센다.
    private double profileRatio(User user, Resume resume) {
        return ratio(List.of(
                hasText(user.getProfileImageUrl()),
                hasText(resume.getIntroduce()),
                hasText(resume.getEmail()),
                !resume.getSkills().isEmpty(),
                hasText(resume.getPortfolioUrl())
        ));
    }

    //프로젝트가 아닌 페이지의 본문을 활동으로 본다. 글자 수로 분량을 잰다.
    private double activityRatio(Resume resume) {
        int length = pages(resume)
                .stream()
                .filter(page -> !page.isProject())
                .map(ResumePage::getContent)
                .filter(Objects::nonNull)
                .mapToInt(content -> content.strip().length())
                .sum();

        return capped((double) length / ACTIVITY_TARGET_LENGTH);
    }

    //프로젝트 페이지는 개수와 페이지별 충실도를 함께 본다. 반만 쓴 페이지는 반 개로 센다.
    private double projectRatio(Resume resume) {
        double written = pages(resume)
                .stream()
                .filter(ResumePage::isProject)
                .mapToDouble(this::projectPageRatio)
                .sum();

        return capped(written / PROJECT_TARGET_COUNT);
    }

    private double projectPageRatio(ResumePage page) {
        ResumeProject project = page.getProject();

        return ratio(List.of(
                project != null && project.hasName(),
                project != null && hasText(project.getSummary()),
                project != null && hasText(project.getImageUrl()),
                project != null && project.getStartDate() != null && project.getEndDate() != null,
                hasText(page.getContent())
        ));
    }

    private double ratio(List<Boolean> items) {
        long filled = items.stream()
                .filter(Boolean::booleanValue)
                .count();

        return (double) filled / items.size();
    }

    private double capped(double ratio) {
        return Math.min(ratio, 1.0);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private List<ResumePage> pages(Resume resume) {
        return resume.getPages() == null ? List.of() : resume.getPages();
    }

    //제출 이후 상태(SUBMITTED, RELEASED)면 제출한 것으로 본다.
    private boolean isSubmitted(Resume resume) {
        ResumeSubmissionStatus status = resume.getSubmissionStatus();

        return status == ResumeSubmissionStatus.SUBMITTED
                || status == ResumeSubmissionStatus.RELEASED;
    }
}
