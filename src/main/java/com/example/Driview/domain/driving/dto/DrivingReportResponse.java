package com.example.Driview.domain.driving.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DrivingReportResponse {
    private Long sessionId;
    private Integer score;
    private String grade;
    private Integer laneScore;
    private Integer focusScore;
    private Integer speedScore;
    private long laneViolationCount;
    private long drowsinessCount;
    private long speedViolationCount;
    private long hardBrakingCount;
}
