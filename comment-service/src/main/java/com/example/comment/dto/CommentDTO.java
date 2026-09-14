package com.example.comment.dto;

import com.example.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDTO {
    private Long id;
    private Long taskId;
    private String author;
    private String authorAvatarColor;
    private String content;
    private Long parentId;
    private List<CommentDTO> replies = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentDTO() {}

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.taskId = comment.getTaskId();
        this.author = comment.getAuthor();
        this.authorAvatarColor = comment.getAuthorAvatarColor();
        this.content = comment.getContent();
        this.parentId = comment.getParentId();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getAuthorAvatarColor() { return authorAvatarColor; }
    public void setAuthorAvatarColor(String authorAvatarColor) { this.authorAvatarColor = authorAvatarColor; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public List<CommentDTO> getReplies() { return replies; }
    public void setReplies(List<CommentDTO> replies) { this.replies = replies; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
