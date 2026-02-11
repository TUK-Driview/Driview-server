package com.example.Driview.domain.sample.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record SampleResponse(

        @Schema(description = "샘플 ID", example = "1")
        Long id,

        @Schema(description = "샘플 제목", example = "코딩하기")
        String title,

        @Schema(description = "샘플 내용", example = "API 개발하기")
        String content
) {}