package com.example.Driview.domain.driving.entity;

import com.example.Driview.domain.driving.enums.AnalysisStatus;
import com.example.Driview.domain.driving.enums.DrivingStatus;
import com.example.Driview.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "driving_session")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrivingSession {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String origin;
    private String destination;
    private Float distanceKm;
    private Integer durationSec;
    private String frontVideoUrl;
    private String driverVideoUrl;
    private Double startLat;
    private Double startLng;
    private Double endLat;
    private Double endLng;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrivingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus analysisStatus;

    private Integer analysisProgress;

    // ==================== 팩토리 메서드 ====================

    public static DrivingSession start(User user, Double startLat, Double startLng, LocalDateTime startedAt) {
        DrivingSession session = new DrivingSession();
        session.user = user;
        session.startLat = startLat;
        session.startLng = startLng;
        session.startedAt = startedAt;
        session.status = DrivingStatus.IN_PROGRESS;
        session.analysisStatus = AnalysisStatus.PENDING;
        session.analysisProgress = 0;
        return session;
    }

    // ==================== 비즈니스 메서드 ====================

    public void complete(Double endLat, Double endLng, LocalDateTime endedAt, Float distanceKm) {
        this.endLat = endLat;
        this.endLng = endLng;
        this.endedAt = endedAt;
        this.distanceKm = distanceKm;
        this.durationSec = (int) java.time.Duration.between(this.startedAt, endedAt).getSeconds();
        this.status = DrivingStatus.COMPLETED;
    }

    public void fail() {
        this.endedAt = LocalDateTime.now();
        this.status = DrivingStatus.FAILED;
    }

    public void updateAnalysisStatus(AnalysisStatus analysisStatus, int progress) {
        this.analysisStatus = analysisStatus;
        this.analysisProgress = progress;
    }

    // ==================== 상태 확인 메서드 ====================

    public boolean isInProgress() { return this.status == DrivingStatus.IN_PROGRESS; }
    public boolean isCompleted() { return this.status == DrivingStatus.COMPLETED; }
}