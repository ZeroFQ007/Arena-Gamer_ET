package com.example.stationservice.controller;

import com.example.stationservice.model.Station;
import com.example.stationservice.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Estaciones", description = "Operaciones para gestionar estaciones de Arena Gamer")
@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @Operation(summary = "Listar estaciones", description = "Obtiene todas las estaciones registradas")
    @ApiResponse(responseCode = "200", description = "Estaciones obtenidas correctamente")
    @GetMapping
    public ResponseEntity<List<Station>> getAll() {
        return ResponseEntity.ok(stationService.findAll());
    }

    @Operation(summary = "Buscar estación por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estación encontrada"),
            @ApiResponse(responseCode = "404", description = "Estación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Station> getById(
            @Parameter(description = "ID de la estación", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(stationService.findById(id));
    }

    @Operation(summary = "Crear estación", description = "Registra una nueva estación (requiere STAFF)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Estación creada correctamente"),
            @ApiResponse(responseCode = "409", description = "Ya existe una estación con ese nombre")
    })
    @PostMapping
    public ResponseEntity<Station> create(@RequestBody Station station) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stationService.create(station));
    }

    @Operation(summary = "Actualizar estación", description = "Actualiza los datos de una estación (requiere STAFF)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estación actualizada correctamente"),
            @ApiResponse(responseCode = "404", description = "Estación no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Station> update(
            @Parameter(description = "ID de la estación", example = "1")
            @PathVariable Long id,
            @RequestBody Station station) {
        return ResponseEntity.ok(stationService.update(id, station));
    }

    @Operation(summary = "Eliminar estación", description = "Elimina una estación (requiere STAFF)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Estación eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Estación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la estación", example = "1")
            @PathVariable Long id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar estaciones por tipo")
    @ApiResponse(responseCode = "200", description = "Estaciones obtenidas correctamente")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Station>> getByType(
            @Parameter(description = "Tipo de estación: PC o CONSOLE", example = "PC")
            @PathVariable Station.StationType type) {
        return ResponseEntity.ok(stationService.findByType(type));
    }

    @Operation(summary = "Listar estaciones disponibles")
    @ApiResponse(responseCode = "200", description = "Estaciones disponibles obtenidas correctamente")
    @GetMapping("/available")
    public ResponseEntity<List<Station>> getDisponibles() {
        return ResponseEntity.ok(stationService.findDisponibles());
    }
}