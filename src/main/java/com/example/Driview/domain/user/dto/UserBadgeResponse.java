package com.example.Driview.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserBadgeResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private CurrentBadgeResponse currentBadge;
    private List<BadgeSummary> allBadges;
}
