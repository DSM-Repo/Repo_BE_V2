package com.example.repo_be_v2.domain.user.domain;

import java.time.LocalDateTime;

/**
 * 학년도와 기수 계산 규칙.
 *
 * 가입할 때 기수를 정하는 쪽과, 도서관에서 공개 당시 학년을 되짚는 쪽이 같은 규칙을 써야 해서
 * 상수와 계산을 여기 모아둔다.
 */
public final class SchoolYear {

    /**
     * 기수 = 학년도 - 학년 - 이 상수.
     * 2026학년도 2학년이 11기인 것에 맞춰 정했다.
     */
    private static final int COHORT_BASE_YEAR = 2013;

    //학년도는 3월에 시작한다. 1~2월은 아직 전년도 학년도다.
    private static final int START_MONTH = 3;

    private SchoolYear() {
    }

    //3월 이전이면 아직 전년도 학년도다.
    public static int of(LocalDateTime dateTime) {
        return dateTime.getMonthValue() < START_MONTH
                ? dateTime.getYear() - 1
                : dateTime.getYear();
    }

    public static LocalDateTime startOf(int schoolYear) {
        return LocalDateTime.of(schoolYear, START_MONTH, 1, 0, 0);
    }

    /**
     * 가입 시점의 학년도와 학년으로 기수를 정한다.
     *
     * 기수는 한번 정해지면 변하지 않는 값이라 가입할 때 계산해 저장한다.
     * 재학 중에는 학년으로 되계산해도 같은 값이 나오지만, 졸업하면 학년이 멈춰 어긋난다.
     */
    public static int cohortOf(int schoolYear, int grade) {
        return schoolYear - grade - COHORT_BASE_YEAR;
    }

    //공개 당시 학년은 저장하지 않는다. 학년도와 기수가 있으면 역산된다.
    public static int gradeOf(int schoolYear, int cohort) {
        return schoolYear - cohort - COHORT_BASE_YEAR;
    }
}
