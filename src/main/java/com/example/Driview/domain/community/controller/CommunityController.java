package com.example.Driview.domain.community.controller;

import com.example.Driview.domain.community.dto.*;
import com.example.Driview.domain.community.service.CommunityService;
import com.example.Driview.global.common.response.ApiResponse;
import com.example.Driview.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(name = "Community", description = "커뮤니티 API")
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                communityService.getPost(postId, userDetails.getUserId())));
    }

    @GetMapping
    @Operation(summary = "게시글 목록 조회", description = "category 파라미터로 필터링 가능, page/size로 페이징")
    public ResponseEntity<ApiResponse<PostListResponse>> getPosts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(communityService.getPosts(category, page, size)));
    }

    @PostMapping
    @Operation(summary = "게시글 작성")
    public ResponseEntity<ApiResponse<PostCreateResponse>> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글이 작성되었습니다.",
                        communityService.createPost(userDetails.getUserId(), request)));
    }

    @PostMapping("/{postId}/likes")
    @Operation(summary = "게시글 좋아요 토글", description = "이미 좋아요한 경우 취소, 아닌 경우 좋아요")
    public ResponseEntity<ApiResponse<PostLikeResponse>> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                communityService.toggleLike(postId, userDetails.getUserId())));
    }

    @PostMapping("/{postId}/comments")
    @Operation(summary = "댓글 작성")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CommentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("댓글이 작성되었습니다.",
                        communityService.createComment(postId, userDetails.getUserId(), request)));
    }

    @GetMapping("/{postId}/comments")
    @Operation(summary = "댓글 목록 조회")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(communityService.getComments(postId)));
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    @Operation(summary = "댓글 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        communityService.deleteComment(postId, commentId, userDetails.getUserId());
        return ResponseEntity.ok(ApiResponse.success("댓글이 삭제되었습니다.", null));
    }
}
