package com.example.notification.dto;

import com.example.notification.model.Notification;
import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private String recipient;
    private String title;
    private String message;
    private String type;
    private Long resourceId;
    private String resourceType;
    private boolean isRead;
    private LocalDateTime timestamp;
    private String emailStatus;
    private int emailAttempts;

    public NotificationDTO() {}

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.recipient = notification.getRecipient();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.type = notification.getType().name();
        this.resourceId = notification.getResourceId();
        this.resourceType = notification.getResourceType();
        this.isRead = notification.isRead();
        this.timestamp = notification.getTimestamp();
        this.emailStatus=notification.getEmailStatus().name(); this.emailAttempts=notification.getEmailAttempts();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getEmailStatus(){return emailStatus;} public void setEmailStatus(String v){emailStatus=v;}
    public int getEmailAttempts(){return emailAttempts;} public void setEmailAttempts(int v){emailAttempts=v;}
}
