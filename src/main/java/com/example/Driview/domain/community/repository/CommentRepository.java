package com.example.Driview.domain.community.repository;

import com.example.Driview.domain.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost_IdAndDeletedAtIsNullOrderByCreatedAtAsc(Long postId);
}
