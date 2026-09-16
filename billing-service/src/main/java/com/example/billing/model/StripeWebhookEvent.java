package com.example.billing.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stripe_webhook_events")
public class StripeWebhookEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String eventId;

    @Column(nullable = false, length = 100)
    private String eventType;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    protected StripeWebhookEvent() {}

    public StripeWebhookEvent(String eventId, String eventType) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.processedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public LocalDateTime getProcessedAt() { return processedAt; }
}
