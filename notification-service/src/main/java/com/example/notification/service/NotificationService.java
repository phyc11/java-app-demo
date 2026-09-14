package com.example.notification.service;

import com.example.common.exception.ResourceNotFoundException;
import com.example.notification.dto.NotificationDTO;
import com.example.notification.model.Notification;
import com.example.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final Map<String, List<SseEmitter>> emittersMap = new ConcurrentHashMap<>();

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<NotificationDTO> getUserNotifications(String recipient) {
        List<Notification> notifications = notificationRepository.findByRecipientOrderByTimestampDesc(recipient);
        if (notifications.isEmpty()) {
            createInitialSeedNotifications(recipient);
            notifications = notificationRepository.findByRecipientOrderByTimestampDesc(recipient);
        }
        return notifications.stream().map(NotificationDTO::new).collect(Collectors.toList());
    }

    public long getUnreadCount(String recipient) {
        return notificationRepository.countByRecipientAndIsReadFalse(recipient);
    }

    public NotificationDTO sendNotification(String recipient, String title, String message, String type) {
        Notification notification = new Notification(recipient, title, message, type);
        Notification saved = notificationRepository.save(notification);
        NotificationDTO dto = new NotificationDTO(saved);

        sendEmailAlert(recipient, title, message);
        pushSseEvent(recipient, dto);

        return dto;
    }

    public NotificationDTO markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", id));
        notification.setRead(true);
        Notification updated = notificationRepository.save(notification);
        return new NotificationDTO(updated);
    }

    public void markAllAsRead(String recipient) {
        List<Notification> notifications = notificationRepository.findByRecipientOrderByTimestampDesc(recipient);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    public SseEmitter subscribeSse(String recipient) {
        SseEmitter emitter = new SseEmitter(3600000L);
        emittersMap.computeIfAbsent(recipient, k -> Collections.synchronizedList(new ArrayList<>())).add(emitter);

        emitter.onCompletion(() -> removeEmitter(recipient, emitter));
        emitter.onTimeout(() -> removeEmitter(recipient, emitter));
        emitter.onError((e) -> removeEmitter(recipient, emitter));

        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Connected to TaskCraft Real-Time Notification Stream"));
        } catch (IOException e) {
            removeEmitter(recipient, emitter);
        }

        return emitter;
    }

    private void pushSseEvent(String recipient, NotificationDTO dto) {
        List<SseEmitter> emitters = emittersMap.get(recipient);
        if (emitters != null) {
            synchronized (emitters) {
                Iterator<SseEmitter> iterator = emitters.iterator();
                while (iterator.hasNext()) {
                    SseEmitter emitter = iterator.next();
                    try {
                        emitter.send(SseEmitter.event()
                                .name("NOTIFICATION")
                                .data(dto));
                    } catch (Exception e) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    private void removeEmitter(String recipient, SseEmitter emitter) {
        List<SseEmitter> emitters = emittersMap.get(recipient);
        if (emitters != null) {
            emitters.remove(emitter);
        }
    }

    private void sendEmailAlert(String recipient, String title, String message) {
        log.info("[EMAIL ALERT SIMULATION] To: {} | Subject: {} | Body: {}", recipient, title, message);
    }

    private void createInitialSeedNotifications(String recipient) {
        notificationRepository.save(new Notification(
                recipient,
                "Chao mung toi TaskCraft!",
                "Chao mung ban gia nhập he thong TaskCraft Microservices Architecture.",
                "WELCOME"
        ));
        notificationRepository.save(new Notification(
                recipient,
                "Task Sap Den Han",
                "Cong viec 'Setup Microservices Architecture' co thoi han hoan thanh trong hom nay.",
                "DUE_SOON"
        ));
    }
}
