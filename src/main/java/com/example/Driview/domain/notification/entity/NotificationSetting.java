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

    private Boolean driveReportAlert = true;
    private Boolean drowsinessAlert = true;
    private Boolean communityCommentAlert = true;
    private Boolean communityLikeAlert = false;
    private Boolean marketingAlert = false;

    // ==================== 팩토리 메서드 ====================

    public static NotificationSetting createDefault(User user) {
        NotificationSetting setting = new NotificationSetting();
        setting.user = user;
        setting.driveReportAlert = true;
        setting.drowsinessAlert = true;
        setting.communityCommentAlert = true;
        setting.communityLikeAlert = false;
        setting.marketingAlert = false;
        return setting;
    }

    // ==================== 비즈니스 메서드 ====================

    public void update(boolean driveReportAlert, boolean drowsinessAlert,
                       boolean communityCommentAlert, boolean communityLikeAlert,
                       boolean marketingAlert) {
        this.driveReportAlert = driveReportAlert;
        this.drowsinessAlert = drowsinessAlert;
        this.communityCommentAlert = communityCommentAlert;
        this.communityLikeAlert = communityLikeAlert;
        this.marketingAlert = marketingAlert;
    }
}

