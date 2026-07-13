package com.example.userservice.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleIllegalArgument_shouldReturn400() {
        // given
        IllegalArgumentException ex = new IllegalArgumentException("Dato inválido");

        // when
        ResponseEntity<?> response = handler.handleIllegalArgument(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(GlobalExceptionHandler.ErrorResponse.class);
        GlobalExceptionHandler.ErrorResponse body = (GlobalExceptionHandler.ErrorResponse) response.getBody();
        assertThat(body.message()).isEqualTo("Dato inválido");
    }

    @Test
    void handleForbidden_shouldReturn403() {
        // given
        AccessDeniedException ex = new AccessDeniedException("Sin permisos");

        // when
        ResponseEntity<?> response = handler.handleForbidden(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        GlobalExceptionHandler.ErrorResponse body = (GlobalExceptionHandler.ErrorResponse) response.getBody();
        assertThat(body.message()).isEqualTo("Acceso denegado");
    }

    @Test
    void handleResponseStatus_shouldReturnCorrectStatus() {
        // given
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");

        // when
        ResponseEntity<?> response = handler.handleResponseStatus(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        GlobalExceptionHandler.ErrorResponse body = (GlobalExceptionHandler.ErrorResponse) response.getBody();
        assertThat(body.message()).isEqualTo("Usuario no encontrado");
    }

    @Test
    void handleGeneric_shouldReturn500() {
        // given
        Exception ex = new RuntimeException("Error inesperado");

        // when
        ResponseEntity<?> response = handler.handleGeneric(ex);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        GlobalExceptionHandler.ErrorResponse body = (GlobalExceptionHandler.ErrorResponse) response.getBody();
        assertThat(body.message()).isEqualTo("Error interno del servidor");
    }

    @Test
    void errorResponse_shouldStoreMessage() {
        // given / when
        GlobalExceptionHandler.ErrorResponse error = new GlobalExceptionHandler.ErrorResponse("Test error");

        // then
        assertThat(error.message()).isEqualTo("Test error");
    }
}