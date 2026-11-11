package com.example.repo_be_v2.domain.company.domain;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyCodeTest {

    @Test
    void 만든_코드는_헷갈리는_글자가_없는_16자다() {
        for (int i = 0; i < 1000; i++) {
            assertThat(CompanyCode.generate()).matches("[A-HJ-NP-Z2-9]{16}");
        }
    }

    @Test
    void 만들_때마다_다른_코드가_나온다() {
        Set<String> codes = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            codes.add(CompanyCode.generate());
        }

        assertThat(codes).hasSize(1000);
    }

    @Test
    void 보여줄_때는_네_글자씩_끊는다() {
        assertThat(CompanyCode.format("K7M2X9PQ4RTW8HNC")).isEqualTo("K7M2-X9PQ-4RTW-8HNC");
    }

    @Test
    void 보여준_모양_그대로_입력해도_저장한_코드로_돌아온다() {
        String code = CompanyCode.generate();

        assertThat(CompanyCode.normalize(CompanyCode.format(code))).contains(code);
    }

    @Test
    void 구분선과_공백과_소문자는_가리지_않는다() {
        assertThat(CompanyCode.normalize(" k7m2 x9pq-4rtw8hnc\n")).contains("K7M2X9PQ4RTW8HNC");
    }

    @Test
    void 코드일_수_없는_값은_받지_않는다() {
        assertThat(CompanyCode.normalize(null)).isEmpty();
        assertThat(CompanyCode.normalize("")).isEmpty();
        assertThat(CompanyCode.normalize("K7M2-X9PQ-4RTW")).isEmpty();
        assertThat(CompanyCode.normalize("K7M2-X9PQ-4RTW-8HNC-AAAA")).isEmpty();
        //0, 1, I, O는 코드에 쓰지 않는 글자다.
        assertThat(CompanyCode.normalize("K7M2-X9PQ-4RTW-8HN0")).isEmpty();
        assertThat(CompanyCode.normalize("K7M2-X9PQ-4RTW-8HNI")).isEmpty();
        assertThat(CompanyCode.normalize("K7M2-X9PQ-4RTW-8HN!")).isEmpty();
    }
}
