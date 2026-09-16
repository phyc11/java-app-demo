package com.example.billing.repository;

import com.example.billing.model.StripeWebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StripeWebhookEventRepository extends JpaRepository<StripeWebhookEvent, Long> {
    boolean existsByEventId(String eventId);
}
