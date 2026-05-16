package com.example.Driview.domain.driveai.entity;

import com.example.Driview.domain.driving.entity.DrivingSession;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "drive_ai_result")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DriveAiResult {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private DrivingSession session;

    private Integer laneDepartureCount;
    private Double durationSec;
    private LocalDateTime analyzedAt;

    public static DriveAiResult create(DrivingSession session, Integer laneDepartureCount, Double durationSec) {
        DriveAiResult result = new DriveAiResult();
        result.session = session;
        result.laneDepartureCount = laneDepartureCount;
        result.durationSec = durationSec;
        result.analyzedAt = LocalDateTime.now();
        return result;
    }
}
