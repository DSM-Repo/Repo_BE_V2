package com.example.repo_be_v2.global.s3;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 버킷 안에서 이 앱이 쓰는 영역(prefix 아래)을 다룬다.
 *
 * 버킷은 여러 서비스가 같이 쓰는 비공개 버킷이라 객체 URL을 그대로 열면 AccessDenied가 난다.
 * 그래서 DB에는 서명 없는 객체 URL을 저장하고,
 * 응답으로 내보낼 때만 유효시간이 있는 서명 URL로 바꾼다.
 */
@Service
@RequiredArgsConstructor
public class S3Service {

    //이 앱이 올리는 객체는 prefix 바로 아래에 "UUID.확장자" 이름으로 놓인다.
    private static final Pattern FILE_NAME = Pattern.compile("[A-Za-z0-9-]+\\.[A-Za-z0-9]+");

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.prefix}")
    private String prefix;

    @Value("${cloud.aws.s3.presigned-url-expiration}")
    private Duration presignedUrlExpiration;

    //들어온 URL이 이 버킷 것인지 가릴 때 쓴다.
    private String bucketHost;

    @PostConstruct
    void init() {
        prefix = prefix.replaceAll("^/+|/+$", "");

        if (prefix.isBlank()) {
            throw new IllegalStateException("cloud.aws.s3.prefix must not be blank");
        }

        bucketHost = s3Client.utilities()
                .getUrl(GetUrlRequest.builder().bucket(bucket).key(prefix).build())
                .getHost();
    }

    public String keyFor(String fileName) {
        return prefix + "/" + fileName;
    }

    public void upload(MultipartFile file, String key) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
        } catch (IOException | SdkException e) {
            throw new S3UploadFailedException(e);
        }
    }

    public String presignedUrlOf(String key) {
        GetObjectPresignRequest request = GetObjectPresignRequest.builder()
                .signatureDuration(presignedUrlExpiration)
                .getObjectRequest(object -> object.bucket(bucket).key(key))
                .build();

        return s3Presigner.presignGetObject(request)
                .url()
                .toExternalForm();
    }

    /**
     * 저장된 이미지 URL을 응답에 담을 URL로 바꾼다.
     *
     * 이 앱이 올린 이미지면 서명 URL을 새로 만들어 준다.
     * 다른 곳의 이미지나 빈 값은 손대지 않는다.
     */
    public String toViewUrl(String url) {
        return findOwnKey(url)
                .map(this::presignedUrlOf)
                .orElse(url);
    }

    /**
     * 요청에 담겨 온 이미지 URL을 저장할 URL로 바꾼다.
     *
     * 프런트는 업로드나 조회로 받은 서명 URL을 그대로 돌려보낸다.
     * 서명은 곧 만료되므로 떼어내고 객체 URL만 남긴다.
     */
    public String toStoredUrl(String url) {
        return findOwnKey(url)
                .map(this::urlOf)
                .orElse(url);
    }

    private String urlOf(String key) {
        return s3Client.utilities()
                .getUrl(GetUrlRequest.builder().bucket(bucket).key(key).build())
                .toExternalForm();
    }

    /**
     * URL이 이 앱이 올린 객체를 가리키면 그 key를 꺼낸다. 서명이 붙어 있어도 된다.
     *
     * prefix 밖의 객체에 서명해 주면 이력서에 다른 서비스의 객체 URL을 적어 넣는 것만으로
     * 그 비공개 파일을 열어볼 수 있게 된다.
     * 그래서 이 버킷 호스트이고 prefix 바로 아래 파일일 때만 인정한다.
     */
    private Optional<String> findOwnKey(String url) {
        if (url == null || url.isBlank()) {
            return Optional.empty();
        }

        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            return Optional.empty();
        }

        String ownPath = "/" + prefix + "/";

        //포트나 사용자 정보가 끼면 authority가 호스트와 달라져 걸러진다.
        if (!"https".equalsIgnoreCase(uri.getScheme())
                || !bucketHost.equalsIgnoreCase(uri.getRawAuthority())
                || uri.getPath() == null
                || !uri.getPath().startsWith(ownPath)) {
            return Optional.empty();
        }

        String fileName = uri.getPath().substring(ownPath.length());

        if (!FILE_NAME.matcher(fileName).matches()) {
            return Optional.empty();
        }

        return Optional.of(keyFor(fileName));
    }
}
