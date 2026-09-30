package com.example.repo_be_v2.domain.history.service;

import com.example.repo_be_v2.domain.history.domain.History;
import com.example.repo_be_v2.domain.history.domain.repository.HistoryRepository;
import com.example.repo_be_v2.domain.history.service.support.HistoryReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistoryDeleteService {

    private final HistoryRepository historyRepository;
    private final HistoryReader historyReader;

    //히스토리 삭제 (선생님 권한)
    @Transactional
    public void execute(Long teacherId, String historyId) {
        historyReader.getTeacher(teacherId);

        History history = historyReader.getHistory(historyId);

        historyRepository.delete(history);
    }
}
