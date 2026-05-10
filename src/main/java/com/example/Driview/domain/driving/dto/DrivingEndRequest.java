package com.example.Driview.domain.driving.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DrivingEndRequest {

    @NotNull
    private Double endLat;

    @NotNull
    private Double endLng;

    @NotNull
    private LocalDateTime endedAt;

    @NotNull
    private Float distanceKm;
}
