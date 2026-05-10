package com.example.Driview.domain.driving.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DrivingTimelineResponse {
    private Long sessionId;
    private List<TimelineEventResponse> events;
}
