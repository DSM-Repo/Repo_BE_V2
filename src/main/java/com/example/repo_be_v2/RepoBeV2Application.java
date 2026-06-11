package com.example.repo_be_v2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaAuditing
public class RepoBeV2Application {

    public static void main(String[] args) {
        SpringApplication.run(RepoBeV2Application.class, args);
    }

}
