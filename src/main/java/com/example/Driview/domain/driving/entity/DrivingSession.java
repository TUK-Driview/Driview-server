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
    private Integer durationSec;
    private String frontVideoUrl;
    private String driverVideoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrivingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus analysisStatus;

    private Integer analysisProgress;

    // ==================== 팩토리 메서드 ====================

    public static DrivingSession create(User user, LocalDateTime startedAt) {
        DrivingSession session = new DrivingSession();
        session.user = user;
        session.startedAt = startedAt;
        session.status = DrivingStatus.COMPLETED;
        session.analysisStatus = AnalysisStatus.PENDING;
        session.analysisProgress = 0;
        return session;
    }

    // ==================== 비즈니스 메서드 ====================

    public void updateDuration(int durationSec) {
        this.durationSec = durationSec;
        this.endedAt = this.startedAt.plusSeconds(durationSec);
    }

    public void updateAnalysisStatus(AnalysisStatus analysisStatus, int progress) {
        this.analysisStatus = analysisStatus;
        this.analysisProgress = progress;
    }

    public void fail() {
        this.endedAt = LocalDateTime.now();
        this.status = DrivingStatus.FAILED;
    }

    // ==================== 상태 확인 메서드 ====================

    public boolean isCompleted() { return this.status == DrivingStatus.COMPLETED; }
}
