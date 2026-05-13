package com.example.Driview.domain.user.service;

import com.example.Driview.domain.driving.enums.DrivingStatus;
import com.example.Driview.domain.driving.enums.ViolationType;
import com.example.Driview.domain.driving.repository.DrivingSessionRepository;
import com.example.Driview.domain.driving.repository.ViolationEventRepository;
import com.example.Driview.domain.faceai.repository.FaceAiResultRepository;
import com.example.Driview.domain.user.dto.UserStatsResponse;
import com.example.Driview.domain.user.entity.User;
import com.example.Driview.domain.user.repository.UserRepository;
import com.example.Driview.global.common.exception.CustomException;
import com.example.Driview.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DrivingSessionRepository drivingSessionRepository;
    private final ViolationEventRepository violationEventRepository;
    private final FaceAiResultRepository faceAiResultRepository;

    @Transactional(readOnly = true)
    public UserStatsResponse getStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime monthEnd = monthStart.plusMonths(1);

        long monthlyDrives = drivingSessionRepository.countByUserIdAndStatusAndStartedAtBetween(
                userId, DrivingStatus.COMPLETED, monthStart, monthEnd);
        long laneDepartureCount = violationEventRepository.countByUserIdAndType(userId, ViolationType.LANE_DEPARTURE);
        long drowsyCount = violationEventRepository.countByUserIdAndType(userId, ViolationType.DROWSY);

        long totalYawnCount = faceAiResultRepository.sumYawnCountByUserId(userId);

        return new UserStatsResponse(
                user.getAvgScore(),
                user.getTotalDrives(),
                user.getTotalKm(),
                monthlyDrives,
                laneDepartureCount,
                drowsyCount,
                totalYawnCount
        );
    }
}
