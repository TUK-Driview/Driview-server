package com.example.Driview.domain.community.service;

import com.example.Driview.domain.community.dto.*;
import com.example.Driview.domain.community.entity.Comment;
import com.example.Driview.domain.community.entity.Post;
import com.example.Driview.domain.community.entity.PostLike;
import com.example.Driview.domain.community.repository.CommentRepository;
import com.example.Driview.domain.community.repository.PostLikeRepository;
import com.example.Driview.domain.community.repository.PostRepository;
import com.example.Driview.domain.user.entity.User;
import com.example.Driview.domain.user.repository.UserRepository;
import com.example.Driview.global.common.exception.CustomException;
import com.example.Driview.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PostListResponse getPosts(String category, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> postPage = (category == null || category.isBlank())
                ? postRepository.findByDeletedAtIsNull(pageable)
                : postRepository.findByCategoryAndDeletedAtIsNull(category, pageable);

        List<PostSummary> summaries = postPage.getContent().stream()
                .map(p -> new PostSummary(
                        p.getId(),
                        p.getCategory(),
                        p.getTitle(),
                        p.getContent(),
                        p.getUser().getNickname(),
                        p.getLikeCount(),
                        p.getCommentCount(),
                        p.getCreatedAt()
                )).toList();

        return new PostListResponse(page, size, postPage.getTotalElements(), summaries);
    }

    @Transactional
    public PostCreateResponse createPost(Long userId, PostCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Post post = Post.create(user, request.getCategory(), request.getTitle(), request.getContent());
        postRepository.save(post);

        return new PostCreateResponse(post.getId());
    }

    @Transactional
    public PostLikeResponse toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return postLikeRepository.findByPost_IdAndUser_Id(postId, userId)
                .map(like -> {
                    like.cancel();
                    postLikeRepository.delete(like);
                    return new PostLikeResponse(false, post.getLikeCount());
                })
                .orElseGet(() -> {
                    postLikeRepository.save(PostLike.create(post, user));
                    return new PostLikeResponse(true, post.getLikeCount());
                });
    }

    @Transactional
    public CommentResponse createComment(Long postId, Long userId, CommentCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Comment comment = Comment.create(post, user, request.getContent());
        commentRepository.save(comment);

        return new CommentResponse(comment.getId(), user.getNickname(), comment.getContent(), comment.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        return commentRepository.findByPost_IdAndDeletedAtIsNullOrderByCreatedAtAsc(postId)
                .stream()
                .map(c -> new CommentResponse(c.getId(), c.getUser().getNickname(), c.getContent(), c.getCreatedAt()))
                .toList();
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getPost().getId().equals(postId)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }
        if (!comment.isWriter(userId)) {
            throw new CustomException(ErrorCode.COMMENT_ACCESS_DENIED);
        }

        comment.delete();
    }
}
