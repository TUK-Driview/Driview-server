package com.example.Driview.domain.user.controller;

import com.example.Driview.domain.user.dto.LoginRequest;
import com.example.Driview.domain.user.dto.SignUpRequest;
import com.example.Driview.domain.user.dto.TokenResponse;
import com.example.Driview.domain.user.service.AuthService;
import com.example.Driview.global.common.response.ApiResponse;
import com.example.Driview.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "이메일 회원가입")
    public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 완료되었습니다.", null));
    }

    @PostMapping("/login")
    @Operation(summary = "이메일 로그인")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다.", authService.login(request)));
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "Authorization 헤더에 Refresh Token을 Bearer로 전달")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(@RequestHeader("Authorization") String bearerToken) {
        String refreshToken = bearerToken.substring(7);
        return ResponseEntity.ok(ApiResponse.success("토큰이 재발급되었습니다.", authService.reissue(refreshToken)));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("로그아웃이 완료되었습니다.", null));
    }
}