package com.example.Driview.domain.faceai.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "face_ai_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FaceAiEvent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private FaceAiResult result;

    private Double timestampSec;
    private String type;

    // ==================== 팩토리 메서드 ====================

    public static FaceAiEvent of(Double timestampSec, String type) {
        FaceAiEvent event = new FaceAiEvent();
        event.timestampSec = timestampSec;
        event.type = type;
        return event;
    }

    // ==================== 연관관계 편의 메서드 ====================

    void assignResult(FaceAiResult result) {
        this.result = result;
    }
}
