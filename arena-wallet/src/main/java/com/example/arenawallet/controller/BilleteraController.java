package com.example.arenawallet.controller;

import com.example.arenawallet.dto.BilleteraCommand;
import com.example.arenawallet.dto.BilleteraRequest;
import com.example.arenawallet.dto.BilleteraResponse;
import com.example.arenawallet.dto.BilleteraResult;
import com.example.arenawallet.service.BilleteraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Billeteras", description = "Operaciones para gestionar billeteras virtuales en Arena Gamer")
@RestController
@RequestMapping("/api/v1/billeteras")
@Validated
public class BilleteraController {

    private final BilleteraService billeteraService;

    public BilleteraController(BilleteraService billeteraService) {
        this.billeteraService = billeteraService;
    }

    @Operation(summary = "Listar billeteras", description = "Obtiene todas las billeteras registradas")
    @ApiResponse(responseCode = "200", description = "Billeteras obtenidas correctamente")
    @GetMapping
    public ResponseEntity<List<BilleteraResponse>> listar() {
        return ResponseEntity.ok(
                billeteraService.listarTodas().stream()
                        .map(this::toResponse).toList());
    }

    @Operation(summary = "Buscar billetera por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Billetera encontrada"),
            @ApiResponse(responseCode = "404", description = "Billetera no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BilleteraResponse> obtenerPorId(
            @Parameter(description = "ID de la billetera", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(toResponse(billeteraService.obtenerPorId(id)));
    }

    @Operation(summary = "Crear billetera", description = "Registra una nueva billetera verificando que el usuario exista en user-service")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Billetera creada correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no existe en user-service"),
            @ApiResponse(responseCode = "400", description = "Ya existe una billetera para ese usuario")
    })
    @PostMapping
    public ResponseEntity<BilleteraResponse> crear(
            @Valid @RequestBody BilleteraRequest request) {
        BilleteraCommand cmd = new BilleteraCommand(
                request.idUsuario(), request.saldo(), request.puntosFidelizacion());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(billeteraService.crear(cmd)));
    }

    @Operation(summary = "Actualizar billetera")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Billetera actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Billetera no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BilleteraResponse> actualizar(
            @Parameter(description = "ID de la billetera", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody BilleteraRequest request) {
        BilleteraCommand cmd = new BilleteraCommand(
                request.idUsuario(), request.saldo(), request.puntosFidelizacion());
        return ResponseEntity.ok(toResponse(billeteraService.actualizar(id, cmd)));
    }

    @Operation(summary = "Recargar saldo", description = "Recarga saldo a la billetera, iniciada por el usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo recargado correctamente"),
            @ApiResponse(responseCode = "400", description = "El monto debe ser positivo"),
            @ApiResponse(responseCode = "404", description = "Billetera no encontrada")
    })
    @PatchMapping("/{id}/recargas")
    public ResponseEntity<BilleteraResponse> recargarSaldo(
            @Parameter(description = "ID de la billetera", example = "1")
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        if (body == null || !body.containsKey("monto")) {
            throw new IllegalArgumentException("El campo 'monto' es obligatorio");
        }
        Double monto = body.get("monto");
        if (monto == null || monto <= 0) {
            throw new IllegalArgumentException("El monto a recargar debe ser positivo");
        }
        return ResponseEntity.ok(toResponse(billeteraService.recargarSaldo(id, monto)));
    }

    @Operation(summary = "Descontar saldo", description = "Descuenta saldo de la billetera, llamado automáticamente por session-service al finalizar una sesión")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo descontado correctamente"),
            @ApiResponse(responseCode = "400", description = "El monto debe ser positivo o saldo insuficiente"),
            @ApiResponse(responseCode = "404", description = "Billetera no encontrada")
    })
    @PatchMapping("/{id}/descuento")
    public ResponseEntity<BilleteraResponse> descontarSaldo(
            @Parameter(description = "ID de la billetera", example = "1")
            @PathVariable Long id,
            @RequestBody Map<String, Double> body) {
        if (body == null || !body.containsKey("monto")) {
            throw new IllegalArgumentException("El campo 'monto' es obligatorio");
        }
        Double monto = body.get("monto");
        if (monto == null || monto <= 0) {
            throw new IllegalArgumentException("El monto a descontar debe ser positivo");
        }
        return ResponseEntity.ok(toResponse(billeteraService.descontarSaldo(id, monto)));
    }

    @Operation(summary = "Ver historial de transacciones")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "Billetera no encontrada")
    })
    @GetMapping("/{id}/historial")
    public ResponseEntity<?> obtenerHistorial(
            @Parameter(description = "ID de la billetera", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(billeteraService.obtenerHistorial(id));
    }

    @Operation(summary = "Eliminar billetera")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Billetera eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Billetera no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID de la billetera", example = "1")
            @PathVariable Long id) {
        billeteraService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private BilleteraResponse toResponse(BilleteraResult r) {
        return new BilleteraResponse(
                r.id(), r.idUsuario(), r.saldo(), r.puntosFidelizacion());
    }
}