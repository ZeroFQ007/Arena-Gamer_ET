package com.example.userservice.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoTest {

    @Test
    void userRequest_shouldStoreFields() {
        UserRequest request = new UserRequest();
        request.setUsername("Fabry27");
        request.setEmail("fabry27@gmail.com");
        request.setRole("PLAYER");

        assertThat(request.getUsername()).isEqualTo("Fabry27");
        assertThat(request.getEmail()).isEqualTo("fabry27@gmail.com");
        assertThat(request.getRole()).isEqualTo("PLAYER");
    }

    @Test
    void userCommand_shouldStoreFields() {
        UserCommand cmd = new UserCommand("Fabry27", "fabry27@gmail.com", "PLAYER");

        assertThat(cmd.username()).isEqualTo("Fabry27");
        assertThat(cmd.email()).isEqualTo("fabry27@gmail.com");
        assertThat(cmd.role()).isEqualTo("PLAYER");
    }

    @Test
    void userResult_shouldStoreFields() {
        UserResult result = new UserResult(1L, "Fabry27", "fabry27@gmail.com", "PLAYER", true);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.username()).isEqualTo("Fabry27");
        assertThat(result.active()).isTrue();
    }

    @Test
    void userResponse_shouldStoreFields() {
        UserResponse response = new UserResponse(1L, "Fabry27", "fabry27@gmail.com", "PLAYER", true);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.username()).isEqualTo("Fabry27");
        assertThat(response.role()).isEqualTo("PLAYER");
    }

    @Test
    void sendNotificationRequest_shouldStoreFields() {
        SendNotificationRequest req = new SendNotificationRequest(
                "fabry27@gmail.com",
                "Bienvenido Fabry27",
                "EMAIL"
        );

        assertThat(req.getRecipient()).isEqualTo("fabry27@gmail.com");
        assertThat(req.getMessage()).isEqualTo("Bienvenido Fabry27");
        assertThat(req.getChannel()).isEqualTo("EMAIL");
    }
}