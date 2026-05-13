package com.example.Driview.domain.faceai.controller;

import com.example.Driview.domain.faceai.dto.FaceAiResponse;
import com.example.Driview.domain.faceai.service.FaceAiService;
import com.example.Driview.global.common.exception.CustomException;
import com.example.Driview.global.common.exception.ErrorCode;
import com.example.Driview.global.common.response.ApiResponse;
import com.example.Driview.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/faceai")
@RequiredArgsConstructor
@Tag(name = "FaceAI", description = "얼굴 분석 AI API")
public class FaceAiController {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".mp4", ".avi", ".mov", ".mkv");

    private final FaceAiService faceAiService;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "운전자 영상 분석", description = "운전자 영상을 Face AI 서버로 전달하여 졸음 이벤트를 분석합니다.")
    public CompletableFuture<ResponseEntity<ApiResponse<FaceAiResponse>>> analyze(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("sessionId") Long sessionId,
            @RequestPart("file") MultipartFile file) {

        validateVideoExtension(file.getOriginalFilename());

        return faceAiService.analyzeVideo(file, sessionId)
                .thenApply(result -> ResponseEntity.ok(ApiResponse.success("영상 분석이 완료되었습니다.", result)));
    }

    private void validateVideoExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_VIDEO_FORMAT);
        }
        int dotIndex = filename.lastIndexOf('.');
        String ext = dotIndex >= 0 ? filename.substring(dotIndex).toLowerCase() : "";
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new CustomException(ErrorCode.INVALID_VIDEO_FORMAT);
        }
    }
}