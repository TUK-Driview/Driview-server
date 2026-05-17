package com.example.Driview.domain.driving.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "driving_report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrivingReport {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private DrivingSession session;

    private Integer totalScore;
    private Integer laneScore;
    private Integer attentionScore;
    private Integer speedScore;
    private Integer accelScore;
    private String grade;
    private LocalDateTime analyzedAt;

    // ==================== 팩토리 메서드 ====================

    public static DrivingReport create(DrivingSession session,
                                       int laneScore, int attentionScore,
                                       int speedScore, int accelScore) {
        DrivingReport report = new DrivingReport();
        report.session = session;
        report.laneScore = laneScore;
        report.attentionScore = attentionScore;
        report.speedScore = speedScore;
        report.accelScore = accelScore;
        report.totalScore = calcTotalScore(laneScore, attentionScore, speedScore, accelScore);
        report.grade = calcGrade(report.totalScore);
        report.analyzedAt = LocalDateTime.now();
        return report;
    }

    // ==================== 내부 계산 메서드 ====================

    // 각 세부 점수는 100점에서 차감 방식으로 계산
    private static int calcTotalScore(int lane, int attention, int speed, int accel) {
        return Math.max(0, (lane + attention + speed + accel) / 4);
    }

    private static String calcGrade(int score) {
        if (score >= 90) return "S";
        if (score >= 80) return "A";
        if (score >= 70) return "B";
        if (score >= 60) return "C";
        return "D";
    }

    // ==================== 점수 업데이트 메서드 ====================

    public void updateLaneScore(int laneScore) {
        this.laneScore = laneScore;
        this.totalScore = calcTotalScore(this.laneScore, this.attentionScore, this.speedScore, this.accelScore);
        this.grade = calcGrade(this.totalScore);
    }

    public void updateAttentionScore(int attentionScore) {
        this.attentionScore = attentionScore;
        this.totalScore = calcTotalScore(this.laneScore, this.attentionScore, this.speedScore, this.accelScore);
        this.grade = calcGrade(this.totalScore);
    }

    // ==================== 상태 확인 메서드 ====================

    public boolean isPerfect() { return "S".equals(this.grade); }
    public boolean isPassed() { return this.totalScore >= 60; }
}