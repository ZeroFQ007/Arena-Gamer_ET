package com.example.stationservice.config;

import com.example.stationservice.model.Station;
import com.example.stationservice.repository.StationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final StationRepository stationRepository;

    public DataLoader(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @Override
    public void run(String... args) {
        if (stationRepository.count() > 0) {
            return;
        }

        stationRepository.save(new Station(null, "PC-01", Station.StationType.PC,
                Station.StationStatus.OPERATIONAL, "RTX 4090 / i9-13900K / 32GB RAM", true));
        stationRepository.save(new Station(null, "PC-02", Station.StationType.PC,
                Station.StationStatus.OPERATIONAL, "RTX 3080 / i7-12700K / 16GB RAM", true));
        stationRepository.save(new Station(null, "PC-03", Station.StationType.PC,
                Station.StationStatus.MAINTENANCE, "RTX 3070 / i5-12600K / 16GB RAM", false));
        stationRepository.save(new Station(null, "CONSOLE-01", Station.StationType.CONSOLE,
                Station.StationStatus.OPERATIONAL, "PS5 / 4K / 120fps", true));
        stationRepository.save(new Station(null, "CONSOLE-02", Station.StationType.CONSOLE,
                Station.StationStatus.OFFLINE, "Xbox Series X / 4K / 120fps", false));
    }
}