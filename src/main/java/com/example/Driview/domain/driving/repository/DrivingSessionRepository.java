package com.example.Driview.domain.driving.repository;

import com.example.Driview.domain.driving.entity.DrivingSession;
import com.example.Driview.domain.driving.enums.DrivingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface DrivingSessionRepository extends JpaRepository<DrivingSession, Long> {

    @Query("SELECT COUNT(ds) FROM DrivingSession ds WHERE ds.user.id = :userId AND ds.status = :status AND ds.startedAt >= :from AND ds.startedAt < :to")
    long countByUserIdAndStatusAndStartedAtBetween(
            @Param("userId") Long userId,
            @Param("status") DrivingStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}
