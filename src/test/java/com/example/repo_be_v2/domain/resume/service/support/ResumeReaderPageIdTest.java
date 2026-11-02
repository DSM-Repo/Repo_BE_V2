package com.example.repo_be_v2.domain.resume.service.support;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumePageType;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.exception.ResumePageIdDuplicatedException;
import com.example.repo_be_v2.domain.resume.presentation.dto.request.ResumePageRequest;
import com.example.repo_be_v2.domain.user.domain.repository.UserRepository;
import com.example.repo_be_v2.global.s3.S3Service;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class ResumeReaderPageIdTest {

    private final ResumeReader resumeReader = new ResumeReader(
            mock(ResumeRepository.class),
            mock(UserRepository.class),
            mock(S3Service.class)
    );

    @Test
    void 중간에_끼운_새_페이지는_뒤로_밀린_페이지의_id를_가져가지_않는다() {
        Resume stored = storedResume(page("A", 0), page("B", 1));

        List<ResumePage> pages = resumeReader.toResumePages(stored, List.of(
                page("A", 0),
                page("", 1),
                page("B", 2)
        ));

        assertThat(idOf(pages, 0)).isEqualTo("A");
        assertThat(idOf(pages, 1)).isNotIn("A", "B").isNotBlank();
        assertThat(idOf(pages, 2)).isEqualTo("B");
    }

    @Test
    void 끼운_뒤_받은_id를_그대로_다시_저장해도_중복으로_막히지_않는다() {
        Resume stored = storedResume(page("A", 0), page("B", 1));
        List<ResumePage> saved = resumeReader.toResumePages(stored, List.of(
                page("A", 0),
                page(null, 1),
                page("B", 2)
        ));

        List<ResumePage> savedAgain = resumeReader.toResumePages(
                storedResume(saved),
                saved.stream().map(page -> page(page.getId(), page.getIndex())).toList()
        );

        assertThat(savedAgain).extracting(ResumePage::getId).doesNotHaveDuplicates();
    }

    @Test
    void 비워_보낸_페이지는_다른_페이지가_쓰지_않는_같은_자리의_id를_물려받는다() {
        Resume stored = storedResume(page("A", 0), page("B", 1));

        List<ResumePage> pages = resumeReader.toResumePages(stored, List.of(
                page("", 0),
                page(null, 1)
        ));

        assertThat(idOf(pages, 0)).isEqualTo("A");
        assertThat(idOf(pages, 1)).isEqualTo("B");
    }

    @Test
    void 이미_중복으로_저장된_이력서를_id_없이_저장하면_중복이_풀린다() {
        Resume stored = storedResume(
                new ResumePage("B", 0, ResumePageType.FREE, null, "내용"),
                new ResumePage("B", 1, ResumePageType.FREE, null, "내용")
        );

        List<ResumePage> pages = resumeReader.toResumePages(stored, List.of(
                page("", 0),
                page("", 1)
        ));

        assertThat(pages).extracting(ResumePage::getId).doesNotHaveDuplicates();
    }

    @Test
    void 요청_안에_같은_id를_직접_보내면_여전히_막는다() {
        assertThatThrownBy(() -> resumeReader.toResumePages(null, List.of(
                page("A", 0),
                page("A", 1)
        ))).isInstanceOf(ResumePageIdDuplicatedException.class);
    }

    private ResumePageRequest page(String id, int index) {
        return new ResumePageRequest(id, index, ResumePageType.FREE, null, "내용");
    }

    private Resume storedResume(ResumePageRequest... pages) {
        return storedResume(resumeReader.toResumePages(null, List.of(pages)));
    }

    private Resume storedResume(ResumePage... pages) {
        return storedResume(List.of(pages));
    }

    private Resume storedResume(List<ResumePage> pages) {
        return Resume.create(1L, "", "", List.of(), "", null, pages, LocalDateTime.now());
    }

    private String idOf(List<ResumePage> pages, int index) {
        return pages.stream()
                .filter(page -> page.getIndex() == index)
                .findFirst()
                .orElseThrow()
                .getId();
    }
}
