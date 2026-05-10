package com.example.Driview.domain.community.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PostCreateRequest {

    @NotBlank
    private String category;

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
