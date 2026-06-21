package com.cybergamer.notificationservice.controller;

import com.cybergamer.notificationservice.dto.SendNotificationDTO;
import com.cybergamer.notificationservice.entity.NotificationLog;
import com.cybergamer.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Notificaciones", description = "Operaciones para gestionar notificaciones en Arena Gamer")
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Enviar notificación", description = "Envía y registra una notificación. Usado internamente por otros microservicios")
    @ApiResponse(responseCode = "200", description = "Notificación enviada correctamente")
    @PostMapping("/send")
    public ResponseEntity<NotificationLog> sendNotification(@RequestBody SendNotificationDTO request) {
        NotificationLog log = notificationService.sendNotification(request);
        return ResponseEntity.ok(log);
    }

    @Operation(summary = "Ver registro de notificaciones", description = "Obtiene el historial de todas las notificaciones enviadas")
    @ApiResponse(responseCode = "200", description = "Registro obtenido correctamente")
    @GetMapping("/logs")
    public ResponseEntity<List<NotificationLog>> getNotificationLogs() {
        return ResponseEntity.ok(notificationService.getAllLogs());
    }
}