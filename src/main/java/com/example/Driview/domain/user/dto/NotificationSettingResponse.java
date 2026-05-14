package com.example.Driview.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationSettingResponse {
    private Boolean driveReportAlert;
    private Boolean drowsinessAlert;
    private Boolean communityCommentAlert;
    private Boolean communityLikeAlert;
    private Boolean marketingAlert;
}
