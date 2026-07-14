package com.example.stationservice.repository;

import com.example.stationservice.model.Station;
import com.example.stationservice.model.StationMaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationMaintenanceLogRepository extends JpaRepository<StationMaintenanceLog, Long> {
    List<StationMaintenanceLog> findByStation(Station station);
    List<StationMaintenanceLog> findByStationId(Long stationId);
}