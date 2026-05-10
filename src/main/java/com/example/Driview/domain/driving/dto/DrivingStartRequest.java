package com.example.Driview.domain.driving.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DrivingStartRequest {

    @NotNull
    private Double startLat;

    @NotNull
    private Double startLng;

    @NotNull
    private LocalDateTime startedAt;
}
