package com.example.repo_be_v2.global.s3;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 마크다운 본문 안의 URL을 하나씩 바꾼다.
 *
 * 에디터는 본문에 이미지를 넣을 때 업로드 응답의 서명 URL을 ![파일명](URL) 형태로 그대로 박는다.
 * 그 서명은 곧 만료되므로 본문 속 이미지도 저장할 때 서명을 떼고, 내보낼 때 새로 서명해야 한다.
 * 어떤 URL을 바꿀지는 넘겨받은 함수가 정한다. S3Service의 toViewUrl·toStoredUrl은 남의 URL을 그대로 돌려준다.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MarkdownUrls {

    //URL은 공백·괄호·꺾쇠·따옴표·대괄호에서 끊는다. ![이름](URL), <URL>, <img src="URL"> 모두 URL만 잘려 나온다.
    private static final Pattern URL = Pattern.compile("https?://[^\\s()<>\"'\\[\\]]+");

    public static String replace(String markdown, UnaryOperator<String> converter) {
        if (markdown == null || markdown.isEmpty()) {
            return markdown;
        }

        return URL.matcher(markdown)
                .replaceAll(match -> Matcher.quoteReplacement(converter.apply(match.group())));
    }
}
