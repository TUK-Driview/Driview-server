package com.example.Driview.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CurrentBadgeResponse {
    private Long badgeId;
    private String name;
    private String description;
    private LocalDateTime acquiredAt;
}
