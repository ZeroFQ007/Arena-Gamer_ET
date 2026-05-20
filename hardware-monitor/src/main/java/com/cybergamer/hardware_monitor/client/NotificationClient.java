package com.cybergamer.hardware_monitor.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.logging.Logger;

@Service
public class NotificationClient {

    private static final Logger log = Logger.getLogger(NotificationClient.class.getName());
    private final RestClient restClient;

    public NotificationClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8089")
                .build();
    }

    public void sendAlert(String recipient, String message) {
        try {
            Map<String, String> body = Map.of(
                    "recipient", recipient,
                    "message", message,
                    "channel", "EMAIL"
            );
            restClient.post()
                    .uri("/api/v1/notifications/send")
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Alerta enviada a: " + recipient);
        } catch (Exception e) {
            log.severe("Error al enviar alerta de hardware: " + e.getMessage());
        }
    }
}