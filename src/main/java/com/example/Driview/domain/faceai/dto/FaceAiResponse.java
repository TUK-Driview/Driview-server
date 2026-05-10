package com.example.Driview.domain.faceai.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class FaceAiResponse {
    private String sessionId;
    private Integer yawnCount;
    private Double durationSec;
    private List<DrowsinessEvent> drowsinessEvents;
}
