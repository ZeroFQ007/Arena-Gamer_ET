package com.example.sessionservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@Slf4j
public class LoyaltyClient {

    private final RestClient restClient;

    public LoyaltyClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8087")
                .build();
    }

    /**
     * Acredita puntos al usuario en loyalty-service.
     * Regla: 1 punto por cada 10 minutos de sesión completada (mínimo 1 punto).
     */
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
            // No bloqueamos el cierre de sesión si loyalty falla
            log.warn("[LOYALTY] No se pudieron acreditar puntos a usuario id={}: {}", userId, e.getMessage());
        }
    }
}