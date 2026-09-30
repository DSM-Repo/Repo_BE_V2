package com.example.repo_be_v2.domain.history.service;

import com.example.repo_be_v2.domain.history.presentation.dto.response.HistoryResponse;
import com.example.repo_be_v2.domain.history.service.support.HistoryReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryListService {

    private final HistoryReader historyReader;

    //히스토리 목록 조회. 최신 날짜순이다.
    @Transactional(readOnly = true)
    public List<HistoryResponse> execute() {
        return historyReader.getHistories()
                .stream()
                .map(HistoryResponse::from)
                .toList();
    }
}
