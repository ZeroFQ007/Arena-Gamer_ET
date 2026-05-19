package com.cybergamer.tournament_service.client;

import com.cybergamer.tournament_service.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "userService",
        url = "${user.service.url:http://localhost:8081}",
        fallback = UserClientFallback.class
)
public interface UserClient {

    @GetMapping("/api/users/{id}")
    UserResponseDTO getUserById(@PathVariable Long id);
}
