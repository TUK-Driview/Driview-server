package com.example.Driview.domain.driving.controller;

import com.example.Driview.domain.driving.dto.*;
import com.example.Driview.domain.driving.service.DrivingService;
import com.example.Driview.global.common.response.ApiResponse;
import com.example.Driview.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/driving")
@RequiredArgsConstructor
@Tag(name = "Driving", description = "운전 세션 API")
public class DrivingController {

    private final DrivingService drivingService;

    @GetMapping("/session")
    @Operation(summary = "세션 목록 조회", description = "연도/월별 완료된 운전 세션 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<DrivingSessionListResponse>> getSessionList(
            @RequestParam Integer year,
            @RequestParam Integer month,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                drivingService.getSessionList(userDetails.getUserId(), year, month)));
    }

    @GetMapping("/{sessionId}/timeline")
    @Operation(summary = "위험 구간 타임라인 조회", description = "운전 세션의 위반 이벤트 타임라인을 조회합니다.")
    public ResponseEntity<ApiResponse<DrivingTimelineResponse>> getTimeline(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                drivingService.getTimeline(sessionId, userDetails.getUserId())));
    }

    @GetMapping("/{sessionId}/report")
    @Operation(summary = "운행 리포트 상세 조회", description = "운전 세션의 분석 리포트를 조회합니다.")
    public ResponseEntity<ApiResponse<DrivingReportResponse>> getReport(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                drivingService.getReport(sessionId, userDetails.getUserId())));
    }

    @GetMapping("/{sessionId}/status")
    @Operation(summary = "분석 진행 상태 조회", description = "운전 세션의 영상 분석 진행 상태를 조회합니다.")
    public ResponseEntity<ApiResponse<DrivingAnalysisStatusResponse>> getAnalysisStatus(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                drivingService.getAnalysisStatus(sessionId, userDetails.getUserId())));
    }
}
