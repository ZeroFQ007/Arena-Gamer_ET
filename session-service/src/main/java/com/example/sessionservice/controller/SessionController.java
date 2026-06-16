package com.example.sessionservice.controller;

import com.example.sessionservice.dto.SessionResponse;
import com.example.sessionservice.model.Session;
import com.example.sessionservice.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Sesiones", description = "Operaciones para gestionar sesiones de juego en Arena Gamer")
@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Operation(summary = "Listar sesiones", description = "Obtiene todas las sesiones con username y nombre de estación")
    @ApiResponse(responseCode = "200", description = "Sesiones obtenidas correctamente")
    @GetMapping
    public ResponseEntity<List<SessionResponse>> getAll() {
        return ResponseEntity.ok(sessionService.findAllWithDetails());
    }

    @Operation(summary = "Buscar sesión por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión encontrada"),
            @ApiResponse(responseCode = "404", description = "Sesión no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> getById(
            @Parameter(description = "ID de la sesión", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(sessionService.findByIdWithDetails(id));
    }

    @Operation(summary = "Iniciar sesión de juego", description = "Crea una nueva sesión verificando usuario y estación")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sesión iniciada correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario o estación no encontrada"),
            @ApiResponse(responseCode = "409", description = "Usuario o estación ya tiene sesión activa")
    })
    @PostMapping
    public ResponseEntity<Session> startSession(@Valid @RequestBody Session session) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionService.startSession(session));
    }

    @Operation(summary = "Finalizar sesión", description = "Finaliza la sesión, descuenta saldo y acredita puntos de lealtad")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión finalizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Sesión no encontrada"),
            @ApiResponse(responseCode = "400", description = "La sesión no está activa")
    })
    @PutMapping("/{id}/finish")
    public ResponseEntity<Session> finishSession(
            @Parameter(description = "ID de la sesión", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(sessionService.finishSession(id));
    }

    @Operation(summary = "Cancelar sesión", description = "Cancela una sesión activa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sesión cancelada correctamente"),
            @ApiResponse(responseCode = "404", description = "Sesión no encontrada"),
            @ApiResponse(responseCode = "400", description = "La sesión no está activa")
    })
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Session> cancelSession(
            @Parameter(description = "ID de la sesión", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(sessionService.cancelSession(id));
    }

    @Operation(summary = "Buscar sesiones por usuario")
    @ApiResponse(responseCode = "200", description = "Sesiones obtenidas correctamente")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Session>> getByUser(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long userId) {
        return ResponseEntity.ok(sessionService.findByUserId(userId));
    }

    @Operation(summary = "Buscar sesiones por estación")
    @ApiResponse(responseCode = "200", description = "Sesiones obtenidas correctamente")
    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<Session>> getByStation(
            @Parameter(description = "ID de la estación", example = "1")
            @PathVariable Long stationId) {
        return ResponseEntity.ok(sessionService.findByStationId(stationId));
    }

    @Operation(summary = "Listar sesiones activas")
    @ApiResponse(responseCode = "200", description = "Sesiones activas obtenidas correctamente")
    @GetMapping("/active")
    public ResponseEntity<List<Session>> getActivas() {
        return ResponseEntity.ok(sessionService.findActivas());
    }
}