package com.cybergamer.tournament_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
        name = "notification-service",
        url = "http://localhost:8089",
        fallback = NotificationClientFallback.class
)
public interface NotificationClient {

    @PostMapping("/api/v1/notifications/send")
    void sendNotification(@RequestBody Map<String, String> body);
}