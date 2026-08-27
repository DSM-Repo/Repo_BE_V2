package com.example.repo_be_v2.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI repoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("REPO API")
                        .description("REPO 프로젝트의 사용자, 이력서, 피드백 API 문서")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("로그인 응답의 accessToken을 입력하세요. Bearer 접두사는 자동으로 추가됩니다.")
                        ));
    }
}
