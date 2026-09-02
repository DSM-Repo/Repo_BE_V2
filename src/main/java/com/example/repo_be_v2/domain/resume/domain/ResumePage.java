package com.example.repo_be_v2.domain.resume.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ResumePage {

    /**
     * 페이지 고유 id.
     * 피드백이 이 값을 들고 위치를 잡으므로 페이지 순서가 바뀌어도 유지되어야 한다.
     * 저장 요청에 값이 없으면 서버가 발급한다.
     */
    private String id;

    private int index;

    private String content;

    public boolean hasId(String id) {
        return this.id != null && this.id.equals(id);
    }

}
