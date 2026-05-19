package com.example.arenareservas.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(
    name = "inventory-service",
    url = "${inventory.service.url:http://localhost:9001}/api/v1/productos",
    fallback = InventoryClientFallback.class
)
public interface InventoryClient {

    @PatchMapping("/{id}/stock")
    void actualizarStock(@PathVariable("id") Long productoId, Map<String, Integer> body);
}
