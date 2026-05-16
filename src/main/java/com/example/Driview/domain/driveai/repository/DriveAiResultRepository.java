package com.example.Driview.domain.driveai.repository;

import com.example.Driview.domain.driveai.entity.DriveAiResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DriveAiResultRepository extends JpaRepository<DriveAiResult, Long> {

    Optional<DriveAiResult> findBySession_Id(Long sessionId);

    @Query("SELECT COALESCE(SUM(r.laneDepartureCount), 0) FROM DriveAiResult r WHERE r.session.user.id = :userId")
    long sumLaneDepartureCountByUserId(@Param("userId") Long userId);
}
