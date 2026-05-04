package com.example.Driview.domain.user.service;

import com.example.Driview.domain.user.dto.LoginRequest;
import com.example.Driview.domain.user.dto.SignUpRequest;
import com.example.Driview.domain.user.dto.TokenResponse;
import com.example.Driview.domain.user.entity.RefreshToken;
import com.example.Driview.domain.user.entity.User;
import com.example.Driview.domain.user.repository.RefreshTokenRepository;
import com.example.Driview.domain.user.repository.UserRepository;
import com.example.Driview.global.common.exception.CustomException;
import com.example.Driview.global.common.exception.ErrorCode;
import com.example.Driview.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        userRepository.save(User.createEmailUser(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname()
        ));
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.isWithdrawn()) {
            throw new CustomException(ErrorCode.WITHDRAWN_USER);
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.createAccessToken(user.getId());
        String newRefreshToken = jwtProvider.createRefreshToken(user.getId());

        refreshTokenRepository.findByUser_Id(user.getId())
                .ifPresentOrElse(
                        rt -> rt.rotate(newRefreshToken, jwtProvider.getRefreshTokenExpiresAt()),
                        () -> refreshTokenRepository.save(
                                RefreshToken.create(user, newRefreshToken, jwtProvider.getRefreshTokenExpiresAt())
                        )
                );

        return new TokenResponse(accessToken, newRefreshToken);
    }

    @Transactional
    public TokenResponse reissue(String refreshToken) {
        RefreshToken saved = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (saved.isExpired()) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        }

        String newAccessToken = jwtProvider.createAccessToken(saved.getUser().getId());
        String newRefreshToken = jwtProvider.createRefreshToken(saved.getUser().getId());
        saved.rotate(newRefreshToken, jwtProvider.getRefreshTokenExpiresAt());

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUser_Id(userId);
    }
}