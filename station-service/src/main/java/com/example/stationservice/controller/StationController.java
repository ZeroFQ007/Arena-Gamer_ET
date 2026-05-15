package com.example.stationservice.controller;

import com.example.stationservice.model.Station;
import com.example.stationservice.service.StationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stations")
public class StationController {
    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }
    @GetMapping
    public ResponseEntity<List<Station>> getAll() {
        return ResponseEntity.ok(stationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Station> getById(@PathVariable Long id) {
        return ResponseEntity.ok(stationService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Station> create(@Valid @RequestBody Station station) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stationService.create(station));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Station> update(@PathVariable Long id, @Valid @RequestBody Station station) {
        return ResponseEntity.ok(stationService.update(id, station));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Station>> getByType(@PathVariable Station.StationType type) {
        return ResponseEntity.ok(stationService.findByType(type));
    }
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Station>> getByStatus(@PathVariable Station.StationStatus status) {
        return ResponseEntity.ok(stationService.findByStatus(status));
    }
    @GetMapping("/available")
    public ResponseEntity<List<Station>> getDisponibles() {
        return ResponseEntity.ok(stationService.findDisponibles());
    }
}
