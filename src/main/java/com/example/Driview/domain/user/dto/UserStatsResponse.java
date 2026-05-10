package com.example.Driview.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserStatsResponse {
    private Float avgScore;
    private Integer totalDrives;
    private Float totalKm;
    private long monthlyDrives;
    private long laneDepartureCount;
    private long drowsyCount;
}
