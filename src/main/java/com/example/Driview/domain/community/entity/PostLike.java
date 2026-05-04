package com.example.Driview.domain.community.entity;

import com.example.Driview.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_like",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime createdAt;

    // ==================== 팩토리 메서드 ====================

    public static PostLike create(Post post, User user) {
        PostLike like = new PostLike();
        like.post = post;
        like.user = user;
        like.createdAt = LocalDateTime.now();
        post.increaseLikeCount();
        return like;
    }

    // ==================== 비즈니스 메서드 ====================

    public void cancel() { this.post.decreaseLikeCount(); }
}
