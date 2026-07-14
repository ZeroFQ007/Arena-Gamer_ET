package com.example.stationservice.controller;

import com.example.stationservice.model.Station;
import com.example.stationservice.model.StationMaintenanceLog;
import com.example.stationservice.service.StationLinkAssembler;
import com.example.stationservice.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Estaciones", description = "Operaciones para gestionar estaciones de Arena Gamer")
@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;
    private final StationLinkAssembler stationLinkAssembler;

    public StationController(StationService stationService, StationLinkAssembler stationLinkAssembler) {
        this.stationService = stationService;
        this.stationLinkAssembler = stationLinkAssembler;
    }

    @Operation(summary = "Listar estaciones", description = "Obtiene todas las estaciones registradas con enlaces HATEOAS en _links")
    @ApiResponse(responseCode = "200", description = "Estaciones obtenidas correctamente")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Station>>> getAll() {
        List<EntityModel<Station>> stations = stationService.findAll().stream()
                .map(stationLinkAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Station>> collection = CollectionModel.of(stations);
        collection.add(linkTo(methodOn(StationController.class).getAll()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Buscar estación por id", description = "Devuelve la estación con enlaces HATEOAS en _links (self, all, update)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estación encontrada"),
            @ApiResponse(responseCode = "404", description = "Estación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Station>> getById(
            @Parameter(description = "ID de la estación", example = "1")
            @PathVariable Long id) {
        Station station = stationService.findById(id);
        return ResponseEntity.ok(stationLinkAssembler.toModel(station));
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
    @Operation(summary = "Ver logs de mantenimiento", description = "Obtiene el historial de mantenimiento de una estación")
    @ApiResponse(responseCode = "200", description = "Logs obtenidos correctamente")
    @GetMapping("/{id}/maintenance-logs")
    public ResponseEntity<List<StationMaintenanceLog>> getMaintenanceLogs(
            @Parameter(description = "ID de la estación", example = "1")
            @PathVariable Long id) {
        Station station = stationService.findById(id);
        return ResponseEntity.ok(station.getMaintenanceLogs());
    }
}