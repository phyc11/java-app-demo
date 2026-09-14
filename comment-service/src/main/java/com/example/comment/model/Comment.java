package com.example.comment.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", indexes = {
        @Index(name = "idx_comment_task", columnList = "task_id"),
        @Index(name = "idx_comment_parent", columnList = "parent_id")
})
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(name = "author_avatar_color", nullable = false, length = 20)
    private String authorAvatarColor;

    @Column(nullable = false, length = 4000)
    private String content;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Comment() {}

    public Comment(Long taskId, String author, String authorAvatarColor, String content, Long parentId) {
        this.taskId = taskId;
        this.author = author;
        this.authorAvatarColor = authorAvatarColor;
        this.content = content;
        this.parentId = parentId;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
