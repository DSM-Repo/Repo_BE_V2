package com.example.repo_be_v2.domain.image.service;

import com.example.repo_be_v2.domain.image.exception.EmptyImageException;
import com.example.repo_be_v2.domain.image.exception.UnsupportedImageTypeException;
import com.example.repo_be_v2.domain.image.presentation.dto.response.ImageResponse;
import com.example.repo_be_v2.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private static final Map<String, String> EXTENSION_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private final S3Service s3Service;

    /**
     * 이미지 업로드
     *
     * 버킷이 비공개라 바로 미리보기할 수 있게 서명 URL을 돌려준다.
     * 프런트는 이 URL을 저장 요청에 그대로 담으면 되고, 서명은 저장할 때 떼어낸다.
     */
    public ImageResponse uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyImageException();
        }

        String extension = EXTENSION_BY_CONTENT_TYPE.get(file.getContentType());
        if (extension == null) {
            throw new UnsupportedImageTypeException();
        }

        String key = s3Service.keyFor(UUID.randomUUID() + extension);
        s3Service.upload(file, key);

        return new ImageResponse(key, s3Service.presignedUrlOf(key));
    }
}
