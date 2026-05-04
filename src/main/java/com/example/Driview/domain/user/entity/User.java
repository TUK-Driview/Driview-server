package com.example.Driview.domain.user.entity;

import com.example.Driview.domain.user.enums.SocialType;
import com.example.Driview.domain.user.enums.UserStatus;
import com.example.Driview.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password; // 소셜 로그인 유저는 null

    @Column(nullable = false, unique = true)
    private String nickname;

    private String profileEmoji;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType;

    private String providerUserId; // 이메일 로그인 유저는 null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private Float avgScore;
    private Integer totalDrives;
    private Float totalKm;
    private LocalDateTime inactiveDate;
    private LocalDateTime deletedAt;

    // ==================== 팩토리 메서드 ====================

    public static User createEmailUser(String email, String encodedPassword, String nickname) {
        User user = new User();
        user.email = email;
        user.password = encodedPassword;
        user.nickname = nickname;
        user.socialType = SocialType.EMAIL;
        user.status = UserStatus.ACTIVE;
        user.avgScore = 0f;
        user.totalDrives = 0;
        user.totalKm = 0f;
        return user;
    }

    public static User createSocialUser(String email, String nickname,
                                        SocialType socialType, String providerUserId) {
        User user = new User();
        user.email = email;
        user.nickname = nickname;
        user.socialType = socialType;
        user.providerUserId = providerUserId;
        user.status = UserStatus.ACTIVE;
        user.avgScore = 0f;
        user.totalDrives = 0;
        user.totalKm = 0f;
        return user;
    }

    // ==================== 비즈니스 메서드 ====================

    public void updateProfile(String nickname, String profileEmoji) {
        this.nickname = nickname;
        this.profileEmoji = profileEmoji;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void withdraw() {
        this.status = UserStatus.WITHDRAWN;
        this.deletedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.inactiveDate = LocalDateTime.now();
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        this.inactiveDate = null;
    }

    public void updateDrivingStats(float distanceKm, float newAvgScore) {
        this.totalDrives = (this.totalDrives == null ? 0 : this.totalDrives) + 1;
        this.totalKm = (this.totalKm == null ? 0f : this.totalKm) + distanceKm;
        this.avgScore = newAvgScore;
    }

    // ==================== 상태 확인 메서드 ====================

    public boolean isActive() { return this.status == UserStatus.ACTIVE; }
    public boolean isWithdrawn() { return this.status == UserStatus.WITHDRAWN; }
    public boolean isEmailUser() { return this.socialType == SocialType.EMAIL; }
}