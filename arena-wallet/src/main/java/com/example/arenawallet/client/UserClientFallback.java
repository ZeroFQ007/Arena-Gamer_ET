package com.example.arenawallet.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserResponse getUserById(Long id) {
        log.warn("[FALLBACK] user-service no disponible al consultar usuario id={}", id);
        return null;
    }
}