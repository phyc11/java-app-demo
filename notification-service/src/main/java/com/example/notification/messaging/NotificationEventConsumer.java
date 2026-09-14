package com.example.notification.messaging;

import com.example.notification.config.RabbitNotificationConfig;
import com.example.notification.dto.NotificationEvent;
import com.example.notification.model.ProcessedEvent;
import com.example.notification.repository.ProcessedEventRepository;
import com.example.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "notification.rabbitmq.enabled", havingValue = "true")
public class NotificationEventConsumer {
    private final NotificationService notificationService;
    private final ProcessedEventRepository processedEventRepository;

    public NotificationEventConsumer(NotificationService notificationService,
                                     ProcessedEventRepository processedEventRepository) {
        this.notificationService = notificationService;
        this.processedEventRepository = processedEventRepository;
    }

    @RabbitListener(queues = RabbitNotificationConfig.QUEUE)
    @Transactional
    public void consume(NotificationEvent event) {
        if (event.getEventId() == null || event.getEventId().trim().isEmpty()) {
            throw new IllegalArgumentException("Event ID is required");
        }
        String eventId = event.getEventId().trim();
        if (processedEventRepository.existsById(eventId)) return;
        notificationService.processEvent(event);
        processedEventRepository.save(new ProcessedEvent(eventId));
    }
}
