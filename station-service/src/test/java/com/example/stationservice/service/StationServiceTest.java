package com.example.stationservice.service;

import com.example.stationservice.model.Station;
import com.example.stationservice.repository.StationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private StationService stationService;

    @Test
    void findAll_shouldReturnAllStations_whenStationsExist() {
        // given
        Station station1 = new Station(1L, "PC-01", Station.StationType.PC,
                Station.StationStatus.OPERATIONAL, "RTX 4090 / i9-13900K / 32GB RAM", true);
        Station station2 = new Station(2L, "CONSOLE-01", Station.StationType.CONSOLE,
                Station.StationStatus.OPERATIONAL, "PS5 / 4K / 120fps", true);

        when(stationRepository.findAll()).thenReturn(List.of(station1, station2));

        // when
        List<Station> result = stationService.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("PC-01");
        verify(stationRepository).findAll();
    }

    @Test
    void findById_shouldReturnStation_whenStationExists() {
        // given
        Station station = new Station(1L, "PC-01", Station.StationType.PC,
                Station.StationStatus.OPERATIONAL, "RTX 4090 / i9-13900K / 32GB RAM", true);

        when(stationRepository.findById(1L)).thenReturn(Optional.of(station));

        // when
        Station result = stationService.findById(1L);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("PC-01");
        verify(stationRepository).findById(1L);
    }

    @Test
    void findById_shouldThrowException_whenStationDoesNotExist() {
        // given
        when(stationRepository.findById(99L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> stationService.findById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Estacion no encontrada");

        verify(stationRepository).findById(99L);
    }

    @Test
    void create_shouldSaveStation_whenNameDoesNotExist() {
        // given
        Station station = new Station(null, "PC-04", Station.StationType.PC,
                Station.StationStatus.OPERATIONAL, "RTX 4070 / i7 / 16GB RAM", true);
        Station saved = new Station(4L, "PC-04", Station.StationType.PC,
                Station.StationStatus.OPERATIONAL, "RTX 4070 / i7 / 16GB RAM", true);

        when(stationRepository.existsByName("PC-04")).thenReturn(false);
        when(stationRepository.save(any(Station.class))).thenReturn(saved);

        // when
        Station result = stationService.create(station);

        // then
        assertThat(result.getId()).isEqualTo(4L);
        assertThat(result.getName()).isEqualTo("PC-04");
        verify(stationRepository).save(station);
    }

    @Test
    void create_shouldThrowException_whenNameAlreadyExists() {
        // given
        Station station = new Station(null, "PC-01", Station.StationType.PC,
                Station.StationStatus.OPERATIONAL, "RTX 4090 / i9 / 32GB RAM", true);

        when(stationRepository.existsByName("PC-01")).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> stationService.create(station))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Ya existe una estacion");

        verify(stationRepository, never()).save(any(Station.class));
    }

    @Test
    void update_shouldUpdateStation_whenStationExists() {
        // given
        Station existente = new Station(1L, "PC-01", Station.StationType.PC,
                Station.StationStatus.OPERATIONAL, "RTX 4090 / i9 / 32GB RAM", true);
        Station datosNuevos = new Station(null, "PC-01-Updated", Station.StationType.PC,
                Station.StationStatus.MAINTENANCE, "RTX 5090 / i9 / 64GB RAM", false);

        when(stationRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(stationRepository.save(any(Station.class))).thenReturn(existente);

        // when
        Station result = stationService.update(1L, datosNuevos);

        // then
        assertThat(result.getName()).isEqualTo("PC-01-Updated");
        assertThat(result.getStatus()).isEqualTo(Station.StationStatus.MAINTENANCE);
        assertThat(result.isAvailable()).isFalse();
        verify(stationRepository).save(existente);
    }

    @Test
    void delete_shouldDeleteStation_whenStationExists() {
        // given
        when(stationRepository.existsById(1L)).thenReturn(true);

        // when
        stationService.delete(1L);

        // then
        verify(stationRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowException_whenStationDoesNotExist() {
        // given
        when(stationRepository.existsById(99L)).thenReturn(false);

        // when / then
        assertThatThrownBy(() -> stationService.delete(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Estacion no encontrada");

        verify(stationRepository, never()).deleteById(any());
    }

    @Test
    void findByType_shouldReturnStations_whenTypeMatches() {
        // given
        Station consoleStation = new Station(2L, "CONSOLE-01", Station.StationType.CONSOLE,
                Station.StationStatus.OPERATIONAL, "PS5 / 4K / 120fps", true);

        when(stationRepository.findByType(Station.StationType.CONSOLE))
                .thenReturn(List.of(consoleStation));

        // when
        List<Station> result = stationService.findByType(Station.StationType.CONSOLE);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo(Station.StationType.CONSOLE);
        verify(stationRepository).findByType(Station.StationType.CONSOLE);
    }

    @Test
    void findDisponibles_shouldReturnOnlyAvailableStations() {
        // given
        Station available = new Station(1L, "PC-01", Station.StationType.PC,
                Station.StationStatus.OPERATIONAL, "RTX 4090 / i9 / 32GB RAM", true);

        when(stationRepository.findByAvailableTrue()).thenReturn(List.of(available));

        // when
        List<Station> result = stationService.findDisponibles();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).isAvailable()).isTrue();
        verify(stationRepository).findByAvailableTrue();
    }
}