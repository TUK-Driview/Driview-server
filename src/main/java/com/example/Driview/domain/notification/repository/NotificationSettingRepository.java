package com.example.Driview.domain.notification.repository;

import com.example.Driview.domain.notification.entity.NotificationSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationSettingRepository extends JpaRepository<NotificationSetting, Long> {
    Optional<NotificationSetting> findByUser_Id(Long userId);
}
