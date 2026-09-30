package com.example.repo_be_v2.domain.history.service;

import com.example.repo_be_v2.domain.history.domain.History;
import com.example.repo_be_v2.domain.history.domain.repository.HistoryRepository;
import com.example.repo_be_v2.domain.history.presentation.dto.request.HistoryRequest;
import com.example.repo_be_v2.domain.history.presentation.dto.response.HistoryResponse;
import com.example.repo_be_v2.domain.history.service.support.HistoryReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistoryCreateService {

    private final HistoryRepository historyRepository;
    private final HistoryReader historyReader;

    //히스토리 등록 (선생님 권한)
    @Transactional
    public HistoryResponse execute(Long teacherId, HistoryRequest request) {
        historyReader.getTeacher(teacherId);

        History history = historyRepository.save(
                History.create(request.date(), request.content())
        );

        return HistoryResponse.from(history);
    }
}
