package com.example.Driview.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BadgeSummary {
    private Long badgeId;
    private String name;
    private boolean isAcquired;
}
