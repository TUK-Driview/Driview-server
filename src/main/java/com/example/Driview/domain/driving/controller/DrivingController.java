package com.example.Driview.domain.driving.controller;

import com.example.Driview.domain.driving.dto.DrivingAnalysisStatusResponse;
import com.example.Driview.domain.driving.service.DrivingService;
import com.example.Driview.global.common.response.ApiResponse;
import com.example.Driview.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/driving")
@RequiredArgsConstructor
@Tag(name = "Driving", description = "운전 세션 API")
public class DrivingController {

    private final DrivingService drivingService;

    @GetMapping("/{sessionId}/status")
    @Operation(summary = "분석 진행 상태 조회", description = "운전 세션의 영상 분석 진행 상태를 조회합니다.")
    public ResponseEntity<ApiResponse<DrivingAnalysisStatusResponse>> getAnalysisStatus(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                drivingService.getAnalysisStatus(sessionId, userDetails.getUserId())));
    }
}
