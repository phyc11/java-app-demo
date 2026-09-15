package com.example.notification.controller;
import com.example.common.dto.ApiResponse;import com.example.notification.model.NotificationTemplate;import com.example.notification.service.NotificationTemplateService;import org.springframework.web.bind.annotation.*;import java.util.List;
@RestController @RequestMapping("/api/notifications/templates") public class NotificationTemplateController {
 private final NotificationTemplateService service;public NotificationTemplateController(NotificationTemplateService s){service=s;}
 @GetMapping public ApiResponse<List<NotificationTemplate>> list(@RequestHeader("X-Role")String role){admin(role);return ApiResponse.ok("Templates retrieved",service.list());}
 @PutMapping("/{code}") public ApiResponse<NotificationTemplate> save(@PathVariable String code,@RequestBody NotificationTemplate t,@RequestHeader("X-Role")String role){admin(role);t.setCode(code);return ApiResponse.ok("Template saved",service.save(t));}
 private void admin(String role){if(!"ROLE_ADMIN".equals(role))throw new SecurityException("Administrator role required");}
}
