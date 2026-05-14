package com.example.Driview.domain.user.service;

import com.example.Driview.domain.driving.enums.DrivingStatus;
import com.example.Driview.domain.driving.enums.ViolationType;
import com.example.Driview.domain.driving.repository.DrivingSessionRepository;
import com.example.Driview.domain.driving.repository.ViolationEventRepository;
import com.example.Driview.domain.faceai.repository.FaceAiResultRepository;
import com.example.Driview.domain.badge.entity.Badge;
import com.example.Driview.domain.badge.entity.UserBadge;
import com.example.Driview.domain.badge.repository.BadgeRepository;
import com.example.Driview.domain.badge.repository.UserBadgeRepository;
import com.example.Driview.domain.notification.entity.NotificationSetting;
import com.example.Driview.domain.notification.repository.NotificationSettingRepository;
import com.example.Driview.domain.community.repository.PostRepository;
import com.example.Driview.domain.user.dto.BadgeSummary;
import com.example.Driview.domain.user.dto.CurrentBadgeResponse;
import com.example.Driview.domain.user.dto.MyPostListResponse;
import com.example.Driview.domain.user.dto.NotificationSettingResponse;
import com.example.Driview.domain.user.dto.UserBadgeResponse;
import com.example.Driview.domain.user.dto.MyPostSummary;
import com.example.Driview.domain.user.dto.UserProfileResponse;
import com.example.Driview.domain.user.dto.UserStatsResponse;
import com.example.Driview.domain.user.entity.User;
import com.example.Driview.domain.user.repository.UserRepository;
import com.example.Driview.global.common.exception.CustomException;
import com.example.Driview.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DrivingSessionRepository drivingSessionRepository;
    private final ViolationEventRepository violationEventRepository;
    private final FaceAiResultRepository faceAiResultRepository;
    private final PostRepository postRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    @Transactional(readOnly = true)
    public NotificationSettingResponse getNotificationSettings(Long userId) {
        NotificationSetting setting = notificationSettingRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
                    return notificationSettingRepository.save(NotificationSetting.createDefault(user));
                });

        return new NotificationSettingResponse(
                setting.getDriveReportAlert(),
                setting.getDrowsinessAlert(),
                setting.getCommunityCommentAlert(),
                setting.getCommunityLikeAlert(),
                setting.getMarketingAlert()
        );
    }

    @Transactional(readOnly = true)
    public UserBadgeResponse getMyBadges(Long userId) {
        // 유저가 획득한 뱃지 ID 집합
        List<UserBadge> userBadges = userBadgeRepository.findByUser_Id(userId);
        Set<Long> acquiredIds = userBadges.stream()
                .map(ub -> ub.getBadge().getId())
                .collect(Collectors.toSet());

        // 가장 최근 획득한 뱃지 → currentBadge
        CurrentBadgeResponse currentBadge = userBadgeRepository
                .findTopByUser_IdOrderByAcquiredAtDesc(userId)
                .map(ub -> new CurrentBadgeResponse(
                        ub.getBadge().getId(),
                        ub.getBadge().getName(),
                        ub.getBadge().getDescription(),
                        ub.getAcquiredAt()
                ))
                .orElse(null);

        // 전체 뱃지 목록 + 획득 여부
        List<BadgeSummary> allBadges = badgeRepository.findAll().stream()
                .map(b -> new BadgeSummary(b.getId(), b.getName(), acquiredIds.contains(b.getId())))
                .toList();

        return new UserBadgeResponse(currentBadge, allBadges);
    }

    @Transactional(readOnly = true)
    public MyPostListResponse getMyPosts(Long userId) {
        var posts = postRepository.findByUser_IdAndDeletedAtIsNullOrderByCreatedAtDesc(userId)
                .stream()
                .map(p -> new MyPostSummary(
                        p.getId(),
                        p.getCategory(),
                        p.getTitle(),
                        p.getContent(),
                        p.getLikeCount(),
                        p.getCommentCount(),
                        p.getCreatedAt()
                )).toList();

        return new MyPostListResponse(posts.size(), posts);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return new UserProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getAvgScore(),
                user.getTotalDrives(),
                user.getTotalKm(),
                user.getCreatedAt()
        );
    }

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
