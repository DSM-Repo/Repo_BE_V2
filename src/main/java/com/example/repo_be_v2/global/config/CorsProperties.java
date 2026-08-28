package com.example.repo_be_v2.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "cors")
public record CorsProperties(
        List<String> allowedOrigins
) {
    public CorsProperties {
        if (allowedOrigins == null || allowedOrigins.isEmpty()) {
            throw new IllegalArgumentException("CORS 허용 Origin은 하나 이상이어야 합니다.");
        }
        allowedOrigins = List.copyOf(allowedOrigins);
    }
}
