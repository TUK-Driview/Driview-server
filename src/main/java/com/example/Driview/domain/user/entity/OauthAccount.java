package com.example.Driview.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "oauth_account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OauthAccount {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String providerUserId;

    private String accessToken;
    private String refreshToken;
    private LocalDateTime tokenExpiresAt;
    private LocalDateTime createdAt;

    // ==================== 팩토리 메서드 ====================

    public static OauthAccount create(User user, String provider, String providerUserId,
                                      String accessToken, String refreshToken,
                                      LocalDateTime tokenExpiresAt) {
        OauthAccount account = new OauthAccount();
        account.user = user;
        account.provider = provider;
        account.providerUserId = providerUserId;
        account.accessToken = accessToken;
        account.refreshToken = refreshToken;
        account.tokenExpiresAt = tokenExpiresAt;
        account.createdAt = LocalDateTime.now();
        return account;
    }

    // ==================== 비즈니스 메서드 ====================

    public void updateToken(String accessToken, String refreshToken, LocalDateTime tokenExpiresAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenExpiresAt = tokenExpiresAt;
    }

    public boolean isTokenExpired() {
        return this.tokenExpiresAt != null && this.tokenExpiresAt.isBefore(LocalDateTime.now());
    }
}