package com.example.Driview.domain.faceai.entity;

import com.example.Driview.domain.driving.entity.DrivingSession;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "face_ai_result")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FaceAiResult {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private DrivingSession session;

    private Integer yawnCount;
    private Double durationSec;
    private LocalDateTime analyzedAt;

    @OneToMany(mappedBy = "result", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FaceAiEvent> events = new ArrayList<>();

    // ==================== 팩토리 메서드 ====================

    public static FaceAiResult create(DrivingSession session, Integer yawnCount,
                                      Double durationSec, List<FaceAiEvent> events) {
        FaceAiResult result = new FaceAiResult();
        result.session = session;
        result.yawnCount = yawnCount;
        result.durationSec = durationSec;
        result.analyzedAt = LocalDateTime.now();
        events.forEach(e -> e.assignResult(result));
        result.events = events;
        return result;
    }
}
