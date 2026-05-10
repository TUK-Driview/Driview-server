package com.example.Driview.domain.driving.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimelineEventResponse {
    private Integer timestamp;
    private String type;
    private String description;
    private Integer count;
    private Double durationSec;
}
