package com.example.arenareservas.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class InventoryClientFallback implements InventoryClient {

    private static final Logger log = LoggerFactory.getLogger(InventoryClientFallback.class);

    @Override
    public void actualizarStock(Long productoId, Map<String, Integer> body) {
        log.warn("WARN: inventory-service caido. No se pudo actualizar stock para producto {}", productoId);
    }
}
