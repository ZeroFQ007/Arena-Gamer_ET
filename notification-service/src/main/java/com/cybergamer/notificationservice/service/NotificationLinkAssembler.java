package com.cybergamer.notificationservice.service;

import com.cybergamer.notificationservice.controller.NotificationController;
import com.cybergamer.notificationservice.entity.NotificationLog;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NotificationLinkAssembler {

    public EntityModel<NotificationLog> toModel(NotificationLog notif) {
        EntityModel<NotificationLog> model = EntityModel.of(notif);

        // Self apunta al historial de logs
        model.add(linkTo(methodOn(NotificationController.class)
                .getNotificationLogs()).withSelfRel());

        // Siempre se puede enviar una nueva notificación
        model.add(Link.of("/api/v1/notifications/send", "send"));

        return model;
    }
}
