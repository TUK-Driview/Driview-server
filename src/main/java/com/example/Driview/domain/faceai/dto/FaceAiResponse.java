package com.example.Driview.domain.faceai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FaceAiResponse {
    private String filename;
    private Integer yawn_count;
    private List<Double> yawn_timestamps;
    private Double duration_sec;
    private List<DrowsinessEvent> drowsinessEvents;
}
