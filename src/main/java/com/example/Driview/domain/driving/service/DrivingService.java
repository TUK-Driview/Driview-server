package com.example.Driview.domain.driving.service;

import com.example.Driview.domain.driving.dto.*;
import com.example.Driview.domain.driving.entity.DrivingReport;
import com.example.Driview.domain.driving.entity.DrivingSession;
import com.example.Driview.domain.driving.enums.DrivingStatus;
import com.example.Driview.domain.driving.enums.ViolationType;
import com.example.Driview.domain.driving.repository.DrivingReportRepository;
import com.example.Driview.domain.driving.repository.DrivingSessionRepository;
import com.example.Driview.domain.driving.repository.ViolationEventRepository;
import com.example.Driview.domain.faceai.entity.FaceAiResult;
import com.example.Driview.domain.faceai.repository.FaceAiResultRepository;
import com.example.Driview.global.common.exception.CustomException;
import com.example.Driview.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DrivingService {

    private final DrivingSessionRepository drivingSessionRepository;
    private final DrivingReportRepository drivingReportRepository;
    private final ViolationEventRepository violationEventRepository;
    private final FaceAiResultRepository faceAiResultRepository;

    private static final Map<String, String> GRADE_LABEL = Map.of(
            "S", "EXCELLENT",
            "A", "GOOD",
            "B", "AVERAGE",
            "C", "POOR",
            "D", "VERY_POOR"
    );

    private static final Map<ViolationType, String> TYPE_LABEL = Map.of(
            ViolationType.LANE_DEPARTURE, "LANE_DEPARTURE",
            ViolationType.DROWSY, "DROWSINESS",
            ViolationType.SPEEDING, "SPEEDING",
            ViolationType.SUDDEN_BRAKE, "HARD_BRAKING",
            ViolationType.SUDDEN_ACCEL, "SUDDEN_ACCEL"
    );

    private static final Set<ViolationType> DURATION_TYPES = Set.of(ViolationType.DROWSY);

    @Transactional(readOnly = true)
    public DrivingSessionListResponse getSessionList(Long userId, int year, int month) {
        LocalDateTime from = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime to = from.plusMonths(1);

        List<DrivingSession> sessions = drivingSessionRepository.findByUserIdAndStatusAndStartedAtBetween(
                userId, DrivingStatus.COMPLETED, from, to);

        List<DrivingSessionSummary> summaries = sessions.stream().map(session -> {
            Integer score = drivingReportRepository.findBySession_Id(session.getId())
                    .map(DrivingReport::getTotalScore)
                    .orElse(null);
            int durationMin = session.getDurationSec() != null ? session.getDurationSec() / 60 : 0;
            return new DrivingSessionSummary(
                    session.getId(),
                    session.getStartedAt(),
                    durationMin,
                    score
            );
        }).toList();

        return new DrivingSessionListResponse(year, month, summaries);
    }

    @Transactional(readOnly = true)
    public DrivingTimelineResponse getTimeline(Long sessionId, Long userId) {
        DrivingSession session = drivingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        if (!session.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }

        List<TimelineEventResponse> events = violationEventRepository
                .findBySession_IdOrderByOccurredAtSecAsc(sessionId)
                .stream()
                .map(v -> {
                    boolean isDuration = DURATION_TYPES.contains(v.getType());
                    return new TimelineEventResponse(
                            v.getOccurredAtSec(),
                            TYPE_LABEL.getOrDefault(v.getType(), v.getType().name()),
                            v.getLocationDesc(),
                            isDuration ? null : 1,
                            isDuration ? v.getDurationSec() : null
                    );
                })
                .toList();

        return new DrivingTimelineResponse(sessionId, events);
    }

    @Transactional(readOnly = true)
    public DrivingReportResponse getReport(Long sessionId, Long userId) {
        DrivingSession session = drivingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        if (!session.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }

        DrivingReport report = drivingReportRepository.findBySession_Id(sessionId).orElse(null);

        FaceAiResult faceAiResult = faceAiResultRepository.findBySession_Id(sessionId).orElse(null);

        List<DrowsinessEventResponse> drowsinessEvents = faceAiResult != null
                ? faceAiResult.getEvents().stream()
                        .map(e -> new DrowsinessEventResponse(e.getTimestampSec(), e.getType()))
                        .toList()
                : Collections.emptyList();

        return new DrivingReportResponse(
                sessionId,
                report != null ? report.getTotalScore() : null,
                report != null ? GRADE_LABEL.getOrDefault(report.getGrade(), report.getGrade()) : null,
                report != null ? report.getLaneScore() : null,
                report != null ? report.getAttentionScore() : null,
                report != null ? report.getSpeedScore() : null,
                report != null ? violationEventRepository.countBySession_IdAndType(sessionId, ViolationType.LANE_DEPARTURE) : null,
                report != null ? violationEventRepository.countBySession_IdAndType(sessionId, ViolationType.DROWSY) : null,
                report != null ? violationEventRepository.countBySession_IdAndType(sessionId, ViolationType.SPEEDING) : null,
                report != null ? violationEventRepository.countBySession_IdAndType(sessionId, ViolationType.SUDDEN_BRAKE) : null,
                faceAiResult != null ? faceAiResult.getYawnCount() : null,
                faceAiResult != null ? faceAiResult.getDurationSec() : null,
                drowsinessEvents
        );
    }

    @Transactional(readOnly = true)
    public DrivingAnalysisStatusResponse getAnalysisStatus(Long sessionId, Long userId) {
        DrivingSession session = drivingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        if (!session.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }

        return new DrivingAnalysisStatusResponse(
                session.getId(),
                session.getAnalysisStatus(),
                session.getAnalysisProgress()
        );
    }
}
