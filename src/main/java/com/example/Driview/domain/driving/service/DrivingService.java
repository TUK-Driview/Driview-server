package com.example.Driview.domain.driving.service;

import com.example.Driview.domain.driving.dto.*;
import com.example.Driview.domain.driving.entity.DrivingReport;
import com.example.Driview.domain.driving.entity.DrivingSession;
import com.example.Driview.domain.driving.enums.DrivingStatus;
import com.example.Driview.domain.driving.repository.DrivingReportRepository;
import com.example.Driview.domain.driving.repository.DrivingSessionRepository;
import com.example.Driview.domain.user.entity.User;
import com.example.Driview.domain.user.repository.UserRepository;
import com.example.Driview.global.common.exception.CustomException;
import com.example.Driview.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DrivingService {

    private final DrivingSessionRepository drivingSessionRepository;
    private final DrivingReportRepository drivingReportRepository;
    private final UserRepository userRepository;

    @Transactional
    public DrivingStartResponse startDriving(Long userId, DrivingStartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        DrivingSession session = DrivingSession.start(
                user,
                request.getStartLat(),
                request.getStartLng(),
                request.getStartedAt()
        );
        drivingSessionRepository.save(session);

        return new DrivingStartResponse(session.getId());
    }

    @Transactional
    public DrivingEndResponse endDriving(Long sessionId, Long userId, DrivingEndRequest request) {
        DrivingSession session = drivingSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        if (!session.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.SESSION_ACCESS_DENIED);
        }

        session.complete(request.getEndLat(), request.getEndLng(), request.getEndedAt(), request.getDistanceKm());

        int durationMin = session.getDurationSec() / 60;

        return new DrivingEndResponse(
                session.getId(),
                session.getOrigin(),
                session.getDestination(),
                session.getDistanceKm(),
                durationMin
        );
    }

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
                    session.getOrigin(),
                    session.getDestination(),
                    session.getDistanceKm(),
                    durationMin,
                    score
            );
        }).toList();

        return new DrivingSessionListResponse(year, month, summaries);
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
