package com.example.Driview.domain.community.repository;

import com.example.Driview.domain.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByDeletedAtIsNull(Pageable pageable);
    Page<Post> findByCategoryAndDeletedAtIsNull(String category, Pageable pageable);
}
