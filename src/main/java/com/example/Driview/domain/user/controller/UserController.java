package com.example.Driview.domain.user.controller;

import com.example.Driview.domain.user.dto.MyPostListResponse;
import com.example.Driview.domain.user.dto.UserProfileResponse;
import com.example.Driview.domain.user.dto.UserStatsResponse;
import com.example.Driview.domain.user.service.UserService;
import com.example.Driview.global.common.response.ApiResponse;
import com.example.Driview.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "사용자 API")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공",
                userService.getProfile(userDetails.getUserId())));
    }

    @GetMapping("/posts")
    @Operation(summary = "내가 쓴 글 목록", description = "현재 로그인한 사용자가 작성한 게시글 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<MyPostListResponse>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("내 게시글 조회 성공",
                userService.getMyPosts(userDetails.getUserId())));
    }

    @GetMapping("/me/stats")
    @Operation(summary = "홈 통계 조회", description = "현재 로그인한 사용자의 운전 통계를 조회합니다.")
    public ResponseEntity<ApiResponse<UserStatsResponse>> getMyStats(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(userService.getStats(userDetails.getUserId())));
    }
}
