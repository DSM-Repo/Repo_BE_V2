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

    private static final String IMAGE_DIRECTORY = "images/";
    private static final Map<String, String> EXTENSION_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private final S3Service s3Service;

    public ImageResponse uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyImageException();
        }

        String extension = EXTENSION_BY_CONTENT_TYPE.get(file.getContentType());
        if (extension == null) {
            throw new UnsupportedImageTypeException();
        }

        String key = IMAGE_DIRECTORY + UUID.randomUUID() + extension;
        String imageUrl = s3Service.upload(file, key);

        return new ImageResponse(key, imageUrl);
    }
}
