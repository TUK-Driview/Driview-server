package com.example.Driview.domain.driving.service;

import com.example.Driview.domain.driving.dto.DrivingAnalysisStatusResponse;
import com.example.Driview.domain.driving.dto.DrivingEndRequest;
import com.example.Driview.domain.driving.dto.DrivingEndResponse;
import com.example.Driview.domain.driving.dto.DrivingStartRequest;
import com.example.Driview.domain.driving.dto.DrivingStartResponse;
import com.example.Driview.domain.driving.entity.DrivingSession;
import com.example.Driview.domain.driving.repository.DrivingSessionRepository;
import com.example.Driview.domain.user.entity.User;
import com.example.Driview.domain.user.repository.UserRepository;
import com.example.Driview.global.common.exception.CustomException;
import com.example.Driview.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DrivingService {

    private final DrivingSessionRepository drivingSessionRepository;
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
