package com.example.Driview.domain.driving.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DrivingEndResponse {
    private Long sessionId;
    private String departure;
    private String destination;
    private Float distanceKm;
    private Integer durationMin;
}
