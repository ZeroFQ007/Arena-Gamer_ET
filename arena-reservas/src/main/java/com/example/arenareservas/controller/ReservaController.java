package com.example.arenareservas.controller;

import com.example.arenareservas.dto.ReservaCommand;
import com.example.arenareservas.dto.ReservaRequest;
import com.example.arenareservas.dto.ReservaResponse;
import com.example.arenareservas.dto.ReservaResult;
import com.example.arenareservas.service.ReservaLinkAssembler;
import com.example.arenareservas.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Reservas", description = "Operaciones para gestionar reservas de estaciones en Arena Gamer")
@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final ReservaLinkAssembler reservaLinkAssembler;

    public ReservaController(ReservaService reservaService, ReservaLinkAssembler reservaLinkAssembler) {
        this.reservaService = reservaService;
        this.reservaLinkAssembler = reservaLinkAssembler;
    }

    @Operation(summary = "Listar reservas", description = "Obtiene todas las reservas con enlaces HATEOAS en _links")
    @ApiResponse(responseCode = "200", description = "Reservas obtenidas correctamente")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ReservaResponse>>> listar(
            @Parameter(description = "Estado a filtrar: NUEVA, CONFIRMADA, CANCELADA", example = "CONFIRMADA")
            @RequestParam(required = false) String estado) {
        List<ReservaResult> resultado = (estado != null && !estado.isBlank())
                ? reservaService.listarPorEstado(estado)
                : reservaService.listarTodas();

        List<EntityModel<ReservaResponse>> reservas = resultado.stream()
                .map(this::toResponse)
                .map(reservaLinkAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<ReservaResponse>> collection = CollectionModel.of(reservas);
        collection.add(linkTo(methodOn(ReservaController.class).listar(null)).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Buscar reserva por id", description = "Devuelve la reserva con enlaces HATEOAS en _links (self, all, history, change-status)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ReservaResponse>> obtenerPorId(
            @Parameter(description = "ID de la reserva", example = "1")
            @PathVariable Long id) {
        ReservaResponse reserva = toResponse(reservaService.obtenerPorId(id));
        return ResponseEntity.ok(reservaLinkAssembler.toModel(reserva));
    }

    @Operation(summary = "Ver historial de cambios", description = "Obtiene el historial de cambios de estado de una reserva")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @GetMapping("/{id}/history")
    public ResponseEntity<List<?>> obtenerHistorial(
            @Parameter(description = "ID de la reserva", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(reservaService.obtenerHistorial(id));
    }

    @Operation(summary = "Crear reserva", description = "Registra una nueva reserva validando conflictos de horario")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reserva creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Fecha pasada o conflicto de horario")
    })
    @PostMapping
    public ResponseEntity<ReservaResponse> crear(
            @Valid @RequestBody ReservaRequest request) {
        ReservaCommand cmd = new ReservaCommand(
                request.usuarioId(), request.estacionId(),
                request.fecha(), request.bloqueHorario());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(reservaService.crear(cmd)));
    }

    @Operation(summary = "Actualizar reserva", description = "Actualiza fecha, bloque horario o estación de una reserva")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reserva actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponse> actualizar(
            @Parameter(description = "ID de la reserva", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ReservaRequest request) {
        ReservaCommand cmd = new ReservaCommand(
                request.usuarioId(), request.estacionId(),
                request.fecha(), request.bloqueHorario());
        return ResponseEntity.ok(toResponse(reservaService.actualizar(id, cmd)));
    }

    @Operation(summary = "Cambiar estado de reserva", description = "Confirma o cancela una reserva. Al confirmar, descuenta stock en arena-inventory")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "El campo 'estado' es obligatorio"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ReservaResponse> cambiarEstado(
            @Parameter(description = "ID de la reserva", example = "1")
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String nuevoEstado = body.get("estado");
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new IllegalArgumentException("El campo 'estado' es obligatorio");
        }
        String comentario = body.get("comentario");
        return ResponseEntity.ok(
                toResponse(reservaService.cambiarEstado(id, nuevoEstado, comentario)));
    }

    @Operation(summary = "Eliminar reserva")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reserva eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la reserva", example = "1")
            @PathVariable Long id) {
        reservaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private ReservaResponse toResponse(ReservaResult r) {
        return new ReservaResponse(
                r.id(), r.usuarioId(), r.estacionId(),
                r.fecha(), r.bloqueHorario(), r.estado());
    }
}