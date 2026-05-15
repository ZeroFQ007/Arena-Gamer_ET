package com.example.stationservice.service;

import com.example.stationservice.model.Station;
import com.example.stationservice.repository.StationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StationService {
private final StationRepository stationRepository;
public StationService(StationRepository stationRepository) {
    this.stationRepository = stationRepository;
}
public List<Station> findAll() {
    return stationRepository.findAll();
}

public Station findById(Long id) {
    return stationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Estacion no encontrada con id: " + id));
}

public Station create(Station station) {
    if (stationRepository.existsByName(station.getName())) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT, "Ya existe una estacion con este nombre : " + station.getName());
    }
    return stationRepository.save(station);
}
public Station update(Long id, Station datos) {
    Station existente = findById(id);
    existente.setName(datos.getName());
    existente.setType(datos.getType());
    existente.setStatus(datos.getStatus());
    existente.setSpecs(datos.getSpecs());
    existente.setAvailable(datos.isAvailable());
    return stationRepository.save(existente);
}
public void delete(Long id) {
    if  (!stationRepository.existsById(id)) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Estacion no encontrada con id: " + id);
    }
    stationRepository.deleteById(id);
}
public List<Station> findByType(Station.StationType type) {
    return stationRepository.findByType(type);
}
public List<Station> findByStatus(Station.StationStatus status) {
    return stationRepository.findByStatus(status);
}
public List<Station> findDisponibles() {
    return stationRepository.findByAvailableTrue();
}
}
