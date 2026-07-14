package com.example.sessionservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@Slf4j
public class WalletClient {

    private final RestClient restClient;

    public WalletClient(RestClient.Builder builder,
                        @Value("${services.wallet-service.url:http://localhost:8085}") String walletServiceUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        this.restClient = builder
                .baseUrl(walletServiceUrl)
                .requestFactory(factory)
                .build();
    }

    public boolean cobrarSesion(Long userId, Double monto) {
        try {
            WalletResponse[] billeteras = restClient.get()
                    .uri("/api/v1/billeteras")
                    .retrieve()
                    .body(WalletResponse[].class);

            if (billeteras == null || billeteras.length == 0) {
                log.warn("[WALLET] No se encontraron billeteras al cobrar sesión de usuario id={}", userId);
                return false;
            }

            Long billeteraId = null;
            for (WalletResponse b : billeteras) {
                if (b.idUsuario().equals(userId)) {
                    billeteraId = b.id();
                    break;
                }
            }

            if (billeteraId == null) {
                log.warn("[WALLET] Usuario id={} no tiene billetera registrada", userId);
                return false;
            }

            restClient.patch()
                    .uri("/api/v1/billeteras/" + billeteraId + "/descuento")
                    .body(Map.of("monto", monto))
                    .retrieve()
                    .toBodilessEntity();

            log.info("[WALLET] Cobro de ${} realizado a usuario id={} en billetera id={}", monto, userId, billeteraId);
            return true;

        } catch (Exception e) {
            log.error("[WALLET] Error al cobrar sesión de usuario id={}: {}", userId, e.getMessage());
            return false;
        }
    }
}