package com.example.Driview.domain.community.repository;

import com.example.Driview.domain.community.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPost_IdAndUser_Id(Long postId, Long userId);
    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);
}
