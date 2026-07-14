package com.example.sessionservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@Slf4j
public class LoyaltyClient {

    private final RestClient restClient;

    public LoyaltyClient(RestClient.Builder builder,
                         @Value("${services.loyalty-service.url:http://localhost:8087}") String loyaltyServiceUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        this.restClient = builder
                .baseUrl(loyaltyServiceUrl)
                .requestFactory(factory)
                .build();
    }

    public void acreditarPuntos(Long userId, Integer durationMinutes) {
        try {
            int puntos = Math.max(1, durationMinutes / 10);
            Map<String, Object> body = Map.of(
                    "userId", String.valueOf(userId),
                    "amount", puntos,
                    "reason", "Sesión completada (" + durationMinutes + " min)"
            );
            restClient.post()
                    .uri("/api/v1/loyalty/earn")
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
            log.info("[LOYALTY] {} puntos acreditados a usuario id={} por sesión de {} min",
                    puntos, userId, durationMinutes);
        } catch (Exception e) {
            log.warn("[LOYALTY] No se pudieron acreditar puntos a usuario id={}: {}", userId, e.getMessage());
        }
    }
}