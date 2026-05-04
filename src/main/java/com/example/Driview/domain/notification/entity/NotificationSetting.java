package com.example.Driview.domain.notification.entity;

import com.example.Driview.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_setting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Boolean driveReport = true;
    private Boolean communityReply = true;
    private Boolean badgeAcquired = true;

    // ==================== 팩토리 메서드 ====================

    public static NotificationSetting createDefault(User user) {
        NotificationSetting setting = new NotificationSetting();
        setting.user = user;
        setting.driveReport = true;
        setting.communityReply = true;
        setting.badgeAcquired = true;
        return setting;
    }

    // ==================== 비즈니스 메서드 ====================

    public void updateDriveReport(boolean enabled) { this.driveReport = enabled; }
    public void updateCommunityReply(boolean enabled) { this.communityReply = enabled; }
    public void updateBadgeAcquired(boolean enabled) { this.badgeAcquired = enabled; }

    public void updateAll(boolean driveReport, boolean communityReply, boolean badgeAcquired) {
        this.driveReport = driveReport;
        this.communityReply = communityReply;
        this.badgeAcquired = badgeAcquired;
    }
}

