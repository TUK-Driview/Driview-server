package com.example.Driview.domain.driving.entity;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrivingStatus status;

    // ==================== 팩토리 메서드 ====================

    public static DrivingSession start(User user, String origin) {
        DrivingSession session = new DrivingSession();
        session.user = user;
        session.origin = origin;
        session.startedAt = LocalDateTime.now();
        session.status = DrivingStatus.IN_PROGRESS;
        return session;
    }

    // ==================== 비즈니스 메서드 ====================

    public void complete(String destination, Float distanceKm, Integer durationSec,
                         String frontVideoUrl, String driverVideoUrl) {
        this.destination = destination;
        this.distanceKm = distanceKm;
        this.durationSec = durationSec;
        this.frontVideoUrl = frontVideoUrl;
        this.driverVideoUrl = driverVideoUrl;
        this.endedAt = LocalDateTime.now();
        this.status = DrivingStatus.COMPLETED;
    }

    public void fail() {
        this.endedAt = LocalDateTime.now();
        this.status = DrivingStatus.FAILED;
    }

    // ==================== 상태 확인 메서드 ====================

    public boolean isInProgress() { return this.status == DrivingStatus.IN_PROGRESS; }
    public boolean isCompleted() { return this.status == DrivingStatus.COMPLETED; }
    }