package com.example.Driview.domain.community.entity;

import com.example.Driview.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    // ==================== 팩토리 메서드 ====================

    public static Comment create(Post post, User user, String content) {
        Comment comment = new Comment();
        comment.post = post;
        comment.user = user;
        comment.content = content;
        comment.createdAt = LocalDateTime.now();
        post.increaseCommentCount();
        return comment;
    }

    // ==================== 비즈니스 메서드 ====================

    public void update(String content) { this.content = content; }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.post.decreaseCommentCount();
    }

    // ==================== 상태 확인 메서드 ====================

    public boolean isDeleted() { return this.deletedAt != null; }
    public boolean isWriter(Long userId) { return this.user.getId().equals(userId); }
}