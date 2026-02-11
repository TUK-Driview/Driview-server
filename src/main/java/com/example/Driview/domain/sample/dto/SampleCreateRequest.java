package com.example.Driview.domain.sample.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SampleCreateRequest(

        @Schema(description = "샘플 제목", example = "코딩하기")
        @NotBlank(message = "title은 필수입니다.")
        @Size(max = 100, message = "title은 100자 이하여야 합니다.")
        String title,

        @Schema(description = "샘플 내용", example = "API 개발하기")
        @NotBlank(message = "content는 필수입니다.")
        @Size(max = 500, message = "content는 500자 이하여야 합니다.")
        String content
) {}