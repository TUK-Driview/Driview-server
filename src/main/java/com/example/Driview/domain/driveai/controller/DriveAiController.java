package com.example.Driview.domain.driveai.controller;

import com.example.Driview.domain.driveai.dto.DriveAiAnalysisResponse;
import com.example.Driview.domain.driveai.service.DriveAiService;
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
@RequestMapping("/api/driveai")
@RequiredArgsConstructor
@Tag(name = "DriveAI", description = "차선 이탈 분석 AI API")
public class DriveAiController {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".mp4", ".avi", ".mov", ".mkv");

    private final DriveAiService driveAiService;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "전방 영상 분석", description = "전방 영상을 Drive AI 서버로 전달하여 차선 이탈 이벤트를 분석합니다.")
    public CompletableFuture<ResponseEntity<ApiResponse<DriveAiAnalysisResponse>>> analyze(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("file") MultipartFile file) {

        validateVideoExtension(file.getOriginalFilename());

        return driveAiService.analyzeVideo(file, userDetails.getUserId())
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
