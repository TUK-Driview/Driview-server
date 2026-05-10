package com.example.Driview.domain.driving.repository;

import com.example.Driview.domain.driving.entity.DrivingReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrivingReportRepository extends JpaRepository<DrivingReport, Long> {
    Optional<DrivingReport> findBySession_Id(Long sessionId);
}
