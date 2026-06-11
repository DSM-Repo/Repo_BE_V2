package com.example.repo_be_v2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class RepoBeV2Application {

    public static void main(String[] args) {
        SpringApplication.run(RepoBeV2Application.class, args);
    }

}
