package com.example.Driview.domain.driving.dto;

import com.example.Driview.domain.driving.enums.AnalysisStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DrivingAnalysisStatusResponse {
    private Long sessionId;
    private AnalysisStatus status;
    private Integer progress;
}
