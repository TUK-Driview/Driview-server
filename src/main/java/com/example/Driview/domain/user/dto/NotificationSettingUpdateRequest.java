package com.example.Driview.domain.user.dto;

import lombok.Getter;

@Getter
public class NotificationSettingUpdateRequest {
    private Boolean driveReportAlert;
    private Boolean drowsinessAlert;
    private Boolean communityCommentAlert;
    private Boolean communityLikeAlert;
    private Boolean marketingAlert;
}
