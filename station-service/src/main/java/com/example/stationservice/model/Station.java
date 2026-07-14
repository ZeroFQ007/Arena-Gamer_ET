package com.example.stationservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "stations")
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StationStatus status;

    @NotBlank(message = "Las especificaciones no pueden estar vacías")
    @Column(nullable = false)
    private String specs;

    @Column(nullable = false)
    private boolean available = true;

    @JsonIgnore
    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StationMaintenanceLog> maintenanceLogs = new ArrayList<>();

    public Station(Long id, String name, StationType type, StationStatus status, String specs, boolean available) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.specs = specs;
        this.available = available;
    }

    public enum StationType {
        PC, CONSOLE
    }

    public enum StationStatus {
        OPERATIONAL, MAINTENANCE, OFFLINE
    }
}