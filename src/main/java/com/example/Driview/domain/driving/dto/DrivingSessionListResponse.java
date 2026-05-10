package com.example.Driview.domain.driving.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DrivingSessionListResponse {
    private Integer year;
    private Integer month;
    private List<DrivingSessionSummary> sessions;
}
