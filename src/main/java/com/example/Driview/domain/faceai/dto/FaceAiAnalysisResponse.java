package com.example.Driview.domain.faceai.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FaceAiAnalysisResponse {
    private Long sessionId;
    private Integer yawn_count;
    private Double duration_sec;
    private List<DrowsinessEvent> drowsinessEvents;
}
