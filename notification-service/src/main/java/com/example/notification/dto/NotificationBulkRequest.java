package com.example.notification.dto;

import java.util.LinkedHashSet;
import java.util.Set;

public class NotificationBulkRequest {
    private Set<Long> notificationIds = new LinkedHashSet<>();

    public Set<Long> getNotificationIds() { return notificationIds; }
    public void setNotificationIds(Set<Long> notificationIds) { this.notificationIds = notificationIds; }
}
