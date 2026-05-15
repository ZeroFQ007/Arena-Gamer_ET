package com.cybergamer.notificationservice.controller;

import com.cybergamer.notificationservice.dto.SendNotificationDTO;
import com.cybergamer.notificationservice.entity.NotificationLog;
import com.cybergamer.notificationservice.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<NotificationLog> sendNotification(@RequestBody SendNotificationDTO request) {
        NotificationLog log = notificationService.sendNotification(request);
        return ResponseEntity.ok(log);
    }

    @GetMapping("/logs")
    public ResponseEntity<List<NotificationLog>> getNotificationLogs() {
        return ResponseEntity.ok(notificationService.getAllLogs());
    }
}