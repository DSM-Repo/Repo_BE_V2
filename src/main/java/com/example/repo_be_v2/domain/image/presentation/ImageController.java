package com.example.repo_be_v2.domain.image.presentation;

import com.example.repo_be_v2.domain.image.presentation.dto.response.ImageResponse;
import com.example.repo_be_v2.domain.image.service.ImageUploadService;
import com.example.repo_be_v2.global.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
@Tag(name = "Image", description = "S3 이미지 업로드 API")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class ImageController {

    private final ImageUploadService imageUploadService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "이미지 업로드", description = "JPEG, PNG, WebP 이미지를 S3에 업로드합니다.")
    @ApiResponse(responseCode = "201", description = "이미지 업로드 성공", useReturnTypeSchema = true)
    public ImageResponse uploadImage(@RequestParam("image") MultipartFile file) {
        return imageUploadService.uploadImage(file);
    }
}
