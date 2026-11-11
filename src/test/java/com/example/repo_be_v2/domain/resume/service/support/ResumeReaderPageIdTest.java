package com.example.repo_be_v2.domain.resume.service.support;

import com.example.repo_be_v2.domain.resume.domain.Resume;
import com.example.repo_be_v2.domain.resume.domain.ResumePage;
import com.example.repo_be_v2.domain.resume.domain.enums.ResumePageType;
import com.example.repo_be_v2.domain.resume.domain.repository.ResumeRepository;
import com.example.repo_be_v2.domain.resume.exception.ResumePageIndexDuplicatedException;
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
    void 비워_보낸_페이지는_같은_자리에_있던_페이지의_id를_물려받지_않는다() {
        Resume stored = storedResume(page("A", 0), page("B", 1));

        //B를 지우고 그 자리에 새 페이지를 만든 요청
        List<ResumePage> pages = resumeReader.toResumePages(stored, List.of(
                page("A", 0),
                page("", 1)
        ));

        assertThat(idOf(pages, 0)).isEqualTo("A");
        assertThat(idOf(pages, 1)).isNotIn("A", "B").isNotBlank();
    }

    @Test
    void 지운_페이지를_실행_취소로_되살려_저장해도_id가_겹치지_않는다() {
        Resume stored = storedResume(page("A", 0), page("B", 1));

        //B를 지우고 새 페이지 X를 만들어 저장한다.
        List<ResumePage> saved = resumeReader.toResumePages(stored, List.of(
                page("A", 0),
                page("", 1)
        ));
        String xId = idOf(saved, 1);

        //실행 취소로 B가 돌아온 상태를 저장한다. X는 방금 받은 id를 들고 있다.
        List<ResumePage> restored = resumeReader.toResumePages(storedResume(saved), List.of(
                page("A", 0),
                page("B", 1),
                page(xId, 2)
        ));

        assertThat(restored).extracting(ResumePage::getId).containsExactly("A", "B", xId);
    }

    @Test
    void 요청_안에_같은_id가_두_번_오면_거절하지_않고_한쪽에_새_id를_준다() {
        List<ResumePage> pages = resumeReader.toResumePages(null, List.of(
                page("A", 0),
                page("A", 1)
        ));

        assertThat(idOf(pages, 0)).isEqualTo("A");
        assertThat(idOf(pages, 1)).isNotEqualTo("A").isNotBlank();
    }

    @Test
    void 같은_id가_두_번_오면_저장돼_있던_자리의_페이지가_그_id를_지킨다() {
        Resume stored = storedResume(page("A", 0), page("B", 1));

        //앞에 끼어든 페이지가 B의 id를 잘못 들고 온 경우
        List<ResumePage> pages = resumeReader.toResumePages(stored, List.of(
                page("B", 0),
                page("B", 1)
        ));

        assertThat(idOf(pages, 1)).isEqualTo("B");
        assertThat(idOf(pages, 0)).isNotEqualTo("B").isNotBlank();
    }

    @Test
    void 이미_중복으로_저장된_이력서를_받은_그대로_저장하면_중복이_풀린다() {
        Resume stored = storedResume(
                new ResumePage("B", 0, ResumePageType.FREE, null, "내용"),
                new ResumePage("B", 1, ResumePageType.FREE, null, "내용")
        );

        List<ResumePage> pages = resumeReader.toResumePages(stored, List.of(
                page("B", 0),
                page("B", 1)
        ));

        assertThat(pages).extracting(ResumePage::getId).doesNotHaveDuplicates();
        assertThat(idOf(pages, 0)).isEqualTo("B");
    }

    @Test
    void 같은_위치의_페이지가_두_장이면_막는다() {
        assertThatThrownBy(() -> resumeReader.toResumePages(null, List.of(
                page("A", 0),
                page("B", 0)
        ))).isInstanceOf(ResumePageIndexDuplicatedException.class);
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
