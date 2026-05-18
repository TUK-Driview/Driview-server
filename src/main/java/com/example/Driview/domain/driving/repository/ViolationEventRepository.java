package com.example.Driview.domain.driving.repository;

import com.example.Driview.domain.driving.entity.ViolationEvent;
import com.example.Driview.domain.driving.enums.ViolationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ViolationEventRepository extends JpaRepository<ViolationEvent, Long> {

    @Query("SELECT COUNT(v) FROM ViolationEvent v WHERE v.session.user.id = :userId AND v.type = :type")
    long countByUserIdAndType(@Param("userId") Long userId, @Param("type") ViolationType type);

    long countBySession_IdAndType(Long sessionId, ViolationType type);

    List<ViolationEvent> findBySession_IdOrderByOccurredAtSecAsc(Long sessionId);

    void deleteBySession_IdAndType(Long sessionId, ViolationType type);
}
