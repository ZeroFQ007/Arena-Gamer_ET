package com.example.sessionservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
@Slf4j
public class UserClient {

    private final RestClient restClient;

    public UserClient(RestClient.Builder builder,
                      @Value("${services.user-service.url:http://localhost:8081}") String userServiceUrl) {
        this.restClient = builder
                .baseUrl(userServiceUrl)
                .defaultHeader("Authorization", "Basic bW9oYW1tZWRBbGlAYXJlbmFnYW1lci5jbDpzdGFmZjEyMw==")
                .build();
    }

    public Optional<UserResponse> findById(Long userId) {
        try {
            UserResponse response = restClient.get()
                    .uri("/api/users/" + userId)
                    .retrieve()
                    .body(UserResponse.class);
            return Optional.ofNullable(response);
        } catch (Exception e) {
            log.error("Usuario no encontrado con id: {}", userId);
            return Optional.empty();
        }
    }

    public boolean existsUser(Long userId) {
        return findById(userId).isPresent();
    }
}