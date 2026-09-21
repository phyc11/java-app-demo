package com.example.notification.dto;

import java.util.LinkedHashSet;
import java.util.Set;

public class NotificationBroadcastRequest {
    private Set<String> recipients = new LinkedHashSet<>();
    private String title;
    private String message;
    private String type = "SYSTEM";
    private Long resourceId;
    private String resourceType;

    public Set<String> getRecipients() { return recipients; }
    public void setRecipients(Set<String> recipients) { this.recipients = recipients; }
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
}
