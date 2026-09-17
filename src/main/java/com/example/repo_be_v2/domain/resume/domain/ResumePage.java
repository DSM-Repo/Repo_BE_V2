package com.example.repo_be_v2.domain.resume.domain;

import com.example.repo_be_v2.domain.resume.domain.enums.ResumePageType;
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

    private ResumePageType type;

    //PROJECT 페이지에만 있다. 나머지 종류에서는 null이다.
    private ResumeProject project;

    //마크다운 원문. 서버는 해석하지 않고 그대로 저장하고 그대로 돌려준다.
    private String content;

    public boolean hasId(String id) {
        return this.id != null && this.id.equals(id);
    }

    //종류가 생기기 전에 저장된 페이지는 머리말이 없는 자유 페이지로 본다.
    public ResumePageType getType() {
        return type == null ? ResumePageType.FREE : type;
    }

    public boolean isProject() {
        return getType() == ResumePageType.PROJECT;
    }
}
