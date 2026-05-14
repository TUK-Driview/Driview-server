package com.example.Driview.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MyPostListResponse {
    private int totalCount;
    private List<MyPostSummary> posts;
}
