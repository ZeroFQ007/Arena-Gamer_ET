package com.example.sessionservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@Slf4j
public class WalletClient {

    private final RestClient restClient;

    public WalletClient(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://localhost:8085")
                .build();
    }

    /**
     * Descuenta el costo de la sesión de la billetera del usuario.
     * Busca la billetera por idUsuario y llama al endpoint de recarga con monto negativo.
     * Retorna true si el cobro fue exitoso, false en caso de fallo.
     */
    public boolean cobrarSesion(Long userId, Double monto) {
        try {
            // Paso 1: obtener todas las billeteras y filtrar por userId
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

            // Paso 2: descontar saldo (monto negativo = descuento)
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