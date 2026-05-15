package com.example.Driview.domain.driving.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DrivingReportResponse {
    private Long sessionId;
    private Integer score;
    private String grade;
    private Integer laneScore;
    private Integer focusScore;
    private Integer speedScore;
    private Long laneViolationCount;
    private Long drowsinessCount;
    private Long speedViolationCount;
    private Long hardBrakingCount;
    // Face AI 분석 결과
    private Integer yawn_count;
    private Double duration_sec;
    private List<DrowsinessEventResponse> drowsinessEvents;
}
