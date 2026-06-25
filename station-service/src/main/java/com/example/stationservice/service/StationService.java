package com.example.stationservice.service;

import com.example.stationservice.model.Station;
import com.example.stationservice.repository.StationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
public class StationService {
private final StationRepository stationRepository;
public StationService(StationRepository stationRepository) {
    this.stationRepository = stationRepository;
}
public List<Station> findAll() {
    List<Station> stations = stationRepository.findAll();
    log.info("Obtenidas {} estaciones", stations.size());
    return stations;
}

public Station findById(Long id) {
    Station station = stationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Estacion no encontrada con id: " + id));
    log.info("Estacion encontrada: id={}, name={}", id, station.getName());
    return station;
}

public Station create(Station station) {
    if (stationRepository.existsByName(station.getName())) {
        log.warn("Intento de crear estacion con nombre existente: {}", station.getName());
        throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Ya existe una estacion con este nombre : " + station.getName());
    }
    Station saved = stationRepository.save(station);
    log.info("Estacion creada: id={}, name={}", saved.getId(), saved.getName());
    return saved;
}
public Station update(Long id, Station datos) {
    Station existente = findById(id);
    existente.setName(datos.getName());
    existente.setType(datos.getType());
    existente.setStatus(datos.getStatus());
    existente.setSpecs(datos.getSpecs());
    existente.setAvailable(datos.isAvailable());
    Station saved = stationRepository.save(existente);
    log.info("Estacion actualizada: id={}, name={}", id, saved.getName());
    return saved;
}
public void delete(Long id) {
    if  (!stationRepository.existsById(id)) {
        log.warn("Intento de eliminar estacion inexistente: id={}", id);
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Estacion no encontrada con id: " + id);
    }
    stationRepository.deleteById(id);
    log.info("Estacion eliminada: id={}", id);
}
public List<Station> findByType(Station.StationType type) {
    List<Station> stations = stationRepository.findByType(type);
    log.info("Estaciones por tipo {}: {}", type, stations.size());
    return stations;
}
public List<Station> findByStatus(Station.StationStatus status) {
    List<Station> stations = stationRepository.findByStatus(status);
    log.info("Estaciones por estado {}: {}", status, stations.size());
    return stations;
}
public List<Station> findDisponibles() {
    List<Station> stations = stationRepository.findByAvailableTrue();
    log.info("Estaciones disponibles: {}", stations.size());
    return stations;
}
}
