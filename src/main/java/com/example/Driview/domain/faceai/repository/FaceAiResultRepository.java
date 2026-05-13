package com.example.Driview.domain.faceai.repository;

import com.example.Driview.domain.faceai.entity.FaceAiResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FaceAiResultRepository extends JpaRepository<FaceAiResult, Long> {

    Optional<FaceAiResult> findBySession_Id(Long sessionId);

    @Query("SELECT COALESCE(SUM(r.yawnCount), 0) FROM FaceAiResult r WHERE r.session.user.id = :userId")
    long sumYawnCountByUserId(@Param("userId") Long userId);
}
