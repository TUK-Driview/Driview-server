package com.example.Driview.domain.driveai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DriveAiResponse {
    private String filename;
    private Double duration_sec;
    private Integer lane_departure_count;
    private List<Double> lane_departure_timestamps;
}
