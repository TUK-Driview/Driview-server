package com.example.Driview.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private Long userId;
    private String nickname;
    private String email;
    private Float avgScore;
    private Integer totalDriveCount;
    private Float totalDistanceKm;
    private LocalDateTime createdAt;
}
