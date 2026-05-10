package com.example.Driview.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostLikeResponse {
    private boolean liked;
    private Integer likeCount;
}
