package com.example.comment.controller;

import com.example.comment.dto.CommentDTO;
import com.example.comment.dto.UpdateCommentRequest;
import com.example.comment.service.CommentService;
import com.example.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getTaskComments(@PathVariable Long taskId) {
        return ResponseEntity.ok(ApiResponse.ok("Comments retrieved",
                commentService.getTaskCommentTree(taskId)));
    }

    @GetMapping("/task/{taskId}/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getTaskCommentCount(@PathVariable Long taskId) {
        return ResponseEntity.ok(ApiResponse.ok("Comment count retrieved",
                Collections.singletonMap("count", commentService.getTaskCommentCount(taskId))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentDTO>> getComment(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Comment retrieved", commentService.getComment(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommentDTO>> addComment(
            @RequestBody CommentDTO request,
            @RequestHeader(value = "X-User", required = false) String userHeader,
            @RequestHeader(value = "X-Avatar-Color", required = false) String avatarColor,
            Principal principal) {
        CommentDTO created = commentService.addComment(request, resolveAuthor(userHeader, principal), avatarColor);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Comment created", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentDTO>> updateComment(
            @PathVariable Long id,
            @RequestBody UpdateCommentRequest request,
            @RequestHeader(value = "X-User", required = false) String userHeader,
            Principal principal) {
        return ResponseEntity.ok(ApiResponse.ok("Comment updated",
                commentService.updateComment(id, request.getContent(), resolveAuthor(userHeader, principal))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long id,
            @RequestHeader(value = "X-User", required = false) String userHeader,
            Principal principal) {
        commentService.deleteComment(id, resolveAuthor(userHeader, principal));
        return ResponseEntity.ok(ApiResponse.ok("Comment deleted", null));
    }

    private String resolveAuthor(String userHeader, Principal principal) {
        if (userHeader != null && !userHeader.trim().isEmpty()) return userHeader.trim();
        return principal == null ? null : principal.getName();
    }
}
