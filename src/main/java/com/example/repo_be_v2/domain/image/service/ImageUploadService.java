package com.example.repo_be_v2.domain.image.service;

import com.example.repo_be_v2.domain.image.exception.EmptyImageException;
import com.example.repo_be_v2.domain.image.exception.UnsupportedImageTypeException;
import com.example.repo_be_v2.domain.image.presentation.dto.response.ImageResponse;
import com.example.repo_be_v2.global.s3.S3Service;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${cloud.aws.s3.prefix}")
    private String prefix;

    @PostConstruct
    void normalizePrefix() {
        prefix = prefix.replaceAll("^/+|/+$", "");

        if (prefix.isBlank()) {
            throw new IllegalStateException("cloud.aws.s3.prefix must not be blank");
        }
    }

    public ImageResponse uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyImageException();
        }

        String extension = EXTENSION_BY_CONTENT_TYPE.get(file.getContentType());
        if (extension == null) {
            throw new UnsupportedImageTypeException();
        }

        String key = createKey(extension);
        String imageUrl = s3Service.upload(file, key);

        return new ImageResponse(key, imageUrl);
    }

    private String createKey(String extension) {
        return prefix + "/" + UUID.randomUUID() + extension;
    }
}
