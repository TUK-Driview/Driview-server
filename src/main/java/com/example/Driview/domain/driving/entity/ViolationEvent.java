package com.example.Driview.domain.driving.entity;

import com.example.Driview.domain.driving.enums.ViolationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "violation_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ViolationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private DrivingSession session;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ViolationType type;

    private Integer occurredAtSec; // 영상 내 발생 시각 (초)
    private String locationDesc;
    private Double latitude;
    private Double longitude;
    private Integer severity; // 1(낮음) ~ 3(높음)
    private Double durationSec; // 졸음 이벤트 지속 시간 (초)

    // ==================== 팩토리 메서드 ====================

    public static ViolationEvent create(DrivingSession session, ViolationType type,
                                        int occurredAtSec, String locationDesc,
                                        double latitude, double longitude, int severity) {
        ViolationEvent event = new ViolationEvent();
        event.session = session;
        event.type = type;
        event.occurredAtSec = occurredAtSec;
        event.locationDesc = locationDesc;
        event.latitude = latitude;
        event.longitude = longitude;
        event.severity = severity;
        return event;
    }

    // ==================== 상태 확인 메서드 ====================

    public boolean isHighSeverity() { return this.severity != null && this.severity >= 3; }
    public boolean isLaneDeparture() { return this.type == ViolationType.LANE_DEPARTURE; }
}