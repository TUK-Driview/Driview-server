package com.example.Driview.domain.driving.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DrivingSessionSummary {
    private Long sessionId;
    private LocalDateTime startedAt;
    private Integer durationSec;
    private Integer score;
}
