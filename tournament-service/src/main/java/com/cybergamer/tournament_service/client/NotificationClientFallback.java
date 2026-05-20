package com.cybergamer.tournament_service.client;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Logger;

@Component
public class NotificationClientFallback implements NotificationClient {

    private static final Logger log = Logger.getLogger(NotificationClientFallback.class.getName());

    @Override
    public void sendNotification(Map<String, String> body) {
        log.warning("NotificationService no disponible. Notificacion no enviada: " + body.get("message"));
    }
}