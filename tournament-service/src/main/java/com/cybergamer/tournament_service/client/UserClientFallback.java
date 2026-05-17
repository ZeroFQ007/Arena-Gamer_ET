package com.cybergamer.tournament_service.client;

import com.cybergamer.tournament_service.dto.UserResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    private static final Logger log = LoggerFactory.getLogger(UserClientFallback.class);

    @Override
    public UserResponseDTO getUserById(Long id) {
        log.warn("WARN: user-service caído. Retornando fallback DTO para id: {}", id);
        return new UserResponseDTO(id, "Jugador No Disponible", "");
    }
}
