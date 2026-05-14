package com.example.Driview.domain.badge.repository;

import com.example.Driview.domain.badge.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findByUser_Id(Long userId);
    Optional<UserBadge> findTopByUser_IdOrderByAcquiredAtDesc(Long userId);
}
