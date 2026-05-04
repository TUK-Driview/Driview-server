package com.example.Driview.domain.badge.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "badge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private String conditionType; // TOTAL_DRIVES, TOTAL_KM, AVG_SCORE 등

    @Column(nullable = false)
    private Integer conditionValue;

    private String emoji;

    // ==================== 팩토리 메서드 ====================

    public static Badge create(String name, String description,
                               String conditionType, int conditionValue, String emoji) {
        Badge badge = new Badge();
        badge.name = name;
        badge.description = description;
        badge.conditionType = conditionType;
        badge.conditionValue = conditionValue;
        badge.emoji = emoji;
        return badge;
    }

    // ==================== 비즈니스 메서드 ====================

    public boolean isSatisfied(int value) {
        return value >= this.conditionValue;
    }
}