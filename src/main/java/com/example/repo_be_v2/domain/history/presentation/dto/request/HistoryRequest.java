package com.example.repo_be_v2.domain.history.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

//등록과 수정이 같은 값을 받는다. 수정도 두 값을 모두 보내 통째로 바꾼다.
public record HistoryRequest(

        @NotNull(message = "날짜를 입력해야 합니다.")
        LocalDate date,

        @NotBlank(message = "내용을 공백으로 둘 수 없습니다.")
        String content

) {
}
