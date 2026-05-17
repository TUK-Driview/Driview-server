package com.example.Driview.domain.driving.repository;

import com.example.Driview.domain.driving.entity.DrivingReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DrivingReportRepository extends JpaRepository<DrivingReport, Long> {
    Optional<DrivingReport> findBySession_Id(Long sessionId);

    @Query("SELECT COALESCE(AVG(r.totalScore), 0) FROM DrivingReport r WHERE r.session.user.id = :userId AND r.totalScore IS NOT NULL")
    float avgTotalScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM DrivingReport r WHERE r.session.user.id = :userId AND r.totalScore IS NOT NULL")
    int countByUserId(@Param("userId") Long userId);
}
