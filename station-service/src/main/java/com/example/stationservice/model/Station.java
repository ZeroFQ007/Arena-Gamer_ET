package com.example.stationservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public enum StationType {
        PC, CONSOLE
    }

    public enum StationStatus {
        OPERATIONAL, MAINTENANCE, OFFLINE
    }
}