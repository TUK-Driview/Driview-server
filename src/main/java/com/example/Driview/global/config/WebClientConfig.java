package com.example.Driview.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient faceAiWebClient() {
        return WebClient.builder()
                .baseUrl("http://54.206.87.180:8000")
                .build();
    }
}