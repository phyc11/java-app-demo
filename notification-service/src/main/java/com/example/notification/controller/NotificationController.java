package com.example.notification.controller;

import com.example.common.dto.ApiResponse;
import com.example.notification.dto.NotificationDTO;
import com.example.notification.dto.NotificationPreferenceRequest;
import com.example.notification.model.NotificationPreference;
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
    public ApiResponse<List<NotificationDTO>> getMyNotifications(
            @RequestHeader(value = "X-User", required = false) String userHeader, Principal principal) {
        String recipient = resolveUser(userHeader, principal);
        List<NotificationDTO> notifications = notificationService.getUserNotifications(recipient);
        return ApiResponse.ok("Notifications retrieved successfully", notifications);
    }

    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> getUnreadCount(
            @RequestHeader(value = "X-User", required = false) String userHeader, Principal principal) {
        String recipient = resolveUser(userHeader, principal);
        long count = notificationService.getUnreadCount(recipient);
        return ApiResponse.ok("Unread count retrieved", Map.of("unreadCount", count));
    }

    @PostMapping("/send")
    public ApiResponse<NotificationDTO> sendNotification(@RequestBody Map<String, String> body,
            @RequestHeader("X-Role") String role) {
        if (!"ROLE_ADMIN".equals(role)) throw new SecurityException("Administrator role required");
        String recipient = body.getOrDefault("recipient", "admin");
        String title = body.getOrDefault("title", "Thông Báo Mới");
        String message = body.getOrDefault("message", "Nội dung thông báo hệ thống.");
        String type = body.getOrDefault("type", "SYSTEM");

        NotificationDTO dto = notificationService.sendNotification(recipient, title, message, type);
        return ApiResponse.ok("Notification sent successfully", dto);
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<NotificationDTO> markAsRead(@PathVariable Long id,
            @RequestHeader(value = "X-User", required = false) String userHeader, Principal principal) {
        NotificationDTO updated = notificationService.markAsRead(id, resolveUser(userHeader, principal));
        return ApiResponse.ok("Notification marked as read", updated);
    }

    @PostMapping("/read-all")
    public ApiResponse<Void> markAllAsRead(
            @RequestHeader(value = "X-User", required = false) String userHeader, Principal principal) {
        String recipient = resolveUser(userHeader, principal);
        notificationService.markAllAsRead(recipient);
        return ApiResponse.ok("All notifications marked as read", null);
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(
            @RequestHeader(value = "X-User", required = false) String userHeader, Principal principal) {
        String recipient = resolveUser(userHeader, principal);
        return notificationService.subscribeSse(recipient);
    }

    @GetMapping("/preferences")
    public ApiResponse<NotificationPreference> getPreferences(
            @RequestHeader(value = "X-User", required = false) String userHeader, Principal principal) {
        return ApiResponse.ok("Notification preferences retrieved",
                notificationService.getPreference(resolveUser(userHeader, principal)));
    }

    @PutMapping("/preferences")
    public ApiResponse<NotificationPreference> updatePreferences(
            @RequestBody NotificationPreferenceRequest request,
            @RequestHeader(value = "X-User", required = false) String userHeader, Principal principal) {
        return ApiResponse.ok("Notification preferences updated",
                notificationService.updatePreference(resolveUser(userHeader, principal), request));
    }

    private String resolveUser(String userHeader, Principal principal) {
        if (userHeader != null && !userHeader.trim().isEmpty()) return userHeader.trim();
        if (principal != null && principal.getName() != null) return principal.getName();
        throw new IllegalArgumentException("Authenticated user is required");
    }
}
