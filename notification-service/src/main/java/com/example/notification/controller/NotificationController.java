package com.example.notification.controller;

import com.example.common.dto.ApiResponse;
import com.example.notification.dto.NotificationDTO;
import com.example.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<List<NotificationDTO>> getMyNotifications(Principal principal) {
        String recipient = principal != null ? principal.getName() : "admin";
        List<NotificationDTO> notifications = notificationService.getUserNotifications(recipient);
        return ApiResponse.ok("Notifications retrieved successfully", notifications);
    }

    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> getUnreadCount(Principal principal) {
        String recipient = principal != null ? principal.getName() : "admin";
        long count = notificationService.getUnreadCount(recipient);
        return ApiResponse.ok("Unread count retrieved", Map.of("unreadCount", count));
    }

    @PostMapping("/send")
    public ApiResponse<NotificationDTO> sendNotification(@RequestBody Map<String, String> body) {
        String recipient = body.getOrDefault("recipient", "admin");
        String title = body.getOrDefault("title", "Thông Báo Mới");
        String message = body.getOrDefault("message", "Nội dung thông báo hệ thống.");
        String type = body.getOrDefault("type", "SYSTEM");

        NotificationDTO dto = notificationService.sendNotification(recipient, title, message, type);
        return ApiResponse.ok("Notification sent successfully", dto);
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<NotificationDTO> markAsRead(@PathVariable Long id) {
        NotificationDTO updated = notificationService.markAsRead(id);
        return ApiResponse.ok("Notification marked as read", updated);
    }

    @PostMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(Principal principal) {
        String recipient = principal != null ? principal.getName() : "admin";
        notificationService.markAllAsRead(recipient);
        return ApiResponse.ok("All notifications marked as read", null);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(Principal principal) {
        String recipient = principal != null ? principal.getName() : "admin";
        return notificationService.subscribeSse(recipient);
    }
}
