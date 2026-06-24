package com.cybergamer.notificationservice.controller;

import com.cybergamer.notificationservice.dto.SendNotificationDTO;
import com.cybergamer.notificationservice.entity.NotificationLog;
import com.cybergamer.notificationservice.service.NotificationLinkAssembler;
import com.cybergamer.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Notificaciones", description = "Operaciones para gestionar notificaciones en Arena Gamer")
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationLinkAssembler linkAssembler;

    public NotificationController(NotificationService notificationService,
                                   NotificationLinkAssembler linkAssembler) {
        this.notificationService = notificationService;
        this.linkAssembler = linkAssembler;
    }

    @Operation(summary = "Enviar notificación",
               description = "Envía y registra una notificación. Usado internamente por otros microservicios")
    @ApiResponse(responseCode = "200", description = "Notificación enviada correctamente")
    @PostMapping("/send")
    public ResponseEntity<EntityModel<NotificationLog>> sendNotification(
            @RequestBody SendNotificationDTO request) {
        NotificationLog log = notificationService.sendNotification(request);
        return ResponseEntity.ok(linkAssembler.toModel(log));
    }

    @Operation(summary = "Ver registro de notificaciones",
               description = "Obtiene el historial de todas las notificaciones enviadas con enlaces HATEOAS")
    @ApiResponse(responseCode = "200", description = "Registro obtenido correctamente")
    @GetMapping("/logs")
    public ResponseEntity<CollectionModel<EntityModel<NotificationLog>>> getNotificationLogs() {
        List<EntityModel<NotificationLog>> modelos = notificationService.getAllLogs()
                .stream()
                .map(linkAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<NotificationLog>> collection = CollectionModel.of(
                modelos,
                linkTo(methodOn(NotificationController.class).getNotificationLogs()).withSelfRel()
        );

        return ResponseEntity.ok(collection);
    }
}
