package com.example.repo_be_v2.domain.history.presentation.dto.response;

import com.example.repo_be_v2.domain.history.domain.History;

import java.time.LocalDate;

public record HistoryResponse(
        String historyId,
        LocalDate date,
        String content
) {

    public static HistoryResponse from(History history) {
        return new HistoryResponse(
                history.getId(),
                history.getDate(),
                history.getContent()
        );
    }
}
