package com.example.Driview.domain.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostSummary {
    private Long postId;
    private String category;
    private String title;
    private String content;
    private String nickname;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
}
