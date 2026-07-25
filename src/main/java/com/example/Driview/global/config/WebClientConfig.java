package com.example.Driview.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient faceAiWebClient() {
        return WebClient.builder()
                .baseUrl("http://3.27.91.132:8000")
                .build();
    }

    @Bean
    public WebClient driveAiWebClient() {
        return WebClient.builder()
                .baseUrl("http://54.79.110.2:8000")
                .build();
    }
}