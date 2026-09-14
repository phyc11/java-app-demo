package com.example.file.dto;

import java.time.LocalDateTime;

public class FileUploadResponseDto {
    private Long id;
    private String originalFileName;
    private String contentType;
    private Long fileSize;
    private String entityType;
    private Long entityId;
    private Long workspaceId;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
    private String presignedDownloadUrl;
    private Integer expirationMinutes;

    public FileUploadResponseDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public Long getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(Long workspaceId) { this.workspaceId = workspaceId; }

    public String getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(String uploadedBy) { this.uploadedBy = uploadedBy; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public String getPresignedDownloadUrl() { return presignedDownloadUrl; }
    public void setPresignedDownloadUrl(String presignedDownloadUrl) { this.presignedDownloadUrl = presignedDownloadUrl; }

    public Integer getExpirationMinutes() { return expirationMinutes; }
    public void setExpirationMinutes(Integer expirationMinutes) { this.expirationMinutes = expirationMinutes; }
}
