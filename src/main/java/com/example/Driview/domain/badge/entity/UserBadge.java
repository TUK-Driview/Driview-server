package com.example.Driview.domain.badge.entity;

import com.example.Driview.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_badge",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "badge_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    private LocalDateTime acquiredAt;

    // ==================== 팩토리 메서드 ====================

    public static UserBadge create(User user, Badge badge) {
        UserBadge userBadge = new UserBadge();
        userBadge.user = user;
        userBadge.badge = badge;
        userBadge.acquiredAt = LocalDateTime.now();
        return userBadge;
    }
}