package com.example.Driview.domain.driving.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DrowsinessEventResponse {
    private Double timestampSec;
    private String type;
}
