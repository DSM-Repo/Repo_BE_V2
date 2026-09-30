package com.example.repo_be_v2.domain.history.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

/**
 * 히스토리 한 건.
 *
 * 날짜와 내용만 저장한다. 작성자·등록 시각은 추적할 일이 없어 두지 않는다.
 * 같은 날짜끼리의 순서는 _id(ObjectId)가 생성 시각을 담고 있어 그걸로 정한다.
 */
@Document(collection = "histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class History {

    @Id
    private String id;

    @Indexed
    private LocalDate date;

    private String content;

    public static History create(LocalDate date, String content) {
        History history = new History();

        history.date = date;
        history.content = content;

        return history;
    }

    public void update(LocalDate date, String content) {
        this.date = date;
        this.content = content;
    }
}
