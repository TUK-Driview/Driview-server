package com.example.Driview.domain.driveai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DriveAiAnalysisResponse {
    private Long sessionId;
    private Integer lane_departure_count;
    private Double duration_sec;
    private List<Double> lane_departure_timestamps;
}
