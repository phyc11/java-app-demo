package com.example.notification.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "processed_notification_events")
public class ProcessedEvent {
    @Id
    @Column(length = 100)
    private String eventId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime processedAt;

    public ProcessedEvent() {}
    public ProcessedEvent(String eventId) {
        this.eventId = eventId;
        this.processedAt = LocalDateTime.now();
    }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}
