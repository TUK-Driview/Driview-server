package com.example.Driview.domain.community.entity;

import com.example.Driview.domain.user.entity.User;
import com.example.Driview.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private Integer likeCount = 0;
    private Integer commentCount = 0;
    private LocalDateTime deletedAt;

    // ==================== 팩토리 메서드 ====================

    public static Post create(User user, String category, String title, String content) {
        Post post = new Post();
        post.user = user;
        post.category = category;
        post.title = title;
        post.content = content;
        post.likeCount = 0;
        post.commentCount = 0;
        return post;
    }

    // ==================== 비즈니스 메서드 ====================

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void delete() { this.deletedAt = LocalDateTime.now(); }
    public void increaseLikeCount() { this.likeCount++; }
    public void decreaseLikeCount() { if (this.likeCount != null && this.likeCount > 0) this.likeCount--; }
    public void increaseCommentCount() { this.commentCount++; }
    public void decreaseCommentCount() { if (this.commentCount != null && this.commentCount > 0) this.commentCount--; }

    // ==================== 상태 확인 메서드 ====================

    public boolean isDeleted() { return this.deletedAt != null; }
    public boolean isWriter(Long userId) { return this.user.getId().equals(userId); }
}