package com.example.repo_be_v2.domain.company.domain;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Optional;

/**
 * 기업 인증 코드의 모양을 정한다.
 *
 * 코드 하나로 공개된 학생 이력서를 모두 볼 수 있으므로 추측으로는 맞힐 수 없어야 한다.
 * 32가지 글자 16자리라 경우의 수가 2^80이고, 그래서 시도 횟수 제한은 따로 두지 않았다.
 *
 * 사람이 받아 적어 입력하는 값이라 0/O, 1/I처럼 헷갈리는 글자는 쓰지 않고,
 * 보여줄 때는 네 글자씩 끊어 K7M2-X9PQ-4RTW-8HNC처럼 내보낸다.
 */
public final class CompanyCode {

    public static final int LENGTH = 16;

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int GROUP_SIZE = 4;
    private static final SecureRandom RANDOM = new SecureRandom();

    private CompanyCode() {
    }

    //저장하는 모양(구분선 없는 16자)으로 새 코드를 만든다.
    public static String generate() {
        StringBuilder code = new StringBuilder(LENGTH);

        for (int i = 0; i < LENGTH; i++) {
            code.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return code.toString();
    }

    /**
     * 입력받은 코드를 저장하는 모양으로 맞춘다. 코드일 수 없는 값이면 비어 있다.
     *
     * 구분선이나 공백을 넣든 빼든, 소문자로 치든 같은 코드로 받아 준다.
     */
    public static Optional<String> normalize(String input) {
        if (input == null) {
            return Optional.empty();
        }

        String code = input.replaceAll("[\\s-]", "").toUpperCase(Locale.ROOT);

        if (code.length() != LENGTH || !code.chars().allMatch(c -> ALPHABET.indexOf(c) >= 0)) {
            return Optional.empty();
        }

        return Optional.of(code);
    }

    //화면에 보여주는 모양. 네 글자씩 구분선으로 끊는다.
    public static String format(String code) {
        StringBuilder formatted = new StringBuilder(code.length() + code.length() / GROUP_SIZE);

        for (int i = 0; i < code.length(); i++) {
            if (i > 0 && i % GROUP_SIZE == 0) {
                formatted.append('-');
            }
            formatted.append(code.charAt(i));
        }

        return formatted.toString();
    }
}
