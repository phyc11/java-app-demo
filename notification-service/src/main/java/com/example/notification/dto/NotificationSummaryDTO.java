package com.example.notification.dto;

import java.util.LinkedHashMap;
import java.util.Map;

public class NotificationSummaryDTO {
    private long total;
    private long unread;
    private Map<String,Long> unreadByType = new LinkedHashMap<>();

    public NotificationSummaryDTO() {}
    public NotificationSummaryDTO(long total,long unread,Map<String,Long> unreadByType) {
        this.total=total; this.unread=unread; this.unreadByType=unreadByType;
    }
    public long getTotal(){return total;} public void setTotal(long value){total=value;}
    public long getUnread(){return unread;} public void setUnread(long value){unread=value;}
    public Map<String,Long> getUnreadByType(){return unreadByType;} public void setUnreadByType(Map<String,Long> value){unreadByType=value;}
}
