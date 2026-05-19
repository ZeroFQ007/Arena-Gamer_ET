package com.cybergamer.tournament_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tournaments")
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // Ej: "Torneo de Valorant"
    private String game;        // Ej: "Valorant"
    private Integer maxTeams;   // Máximo de equipos
    private Integer currentTeams; // Cuántos van inscritos
    private String status;      // OPEN, IN_PROGRESS, FINISHED

    public Tournament() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGame() { return game; }
    public void setGame(String game) { this.game = game; }
    public Integer getMaxTeams() { return maxTeams; }
    public void setMaxTeams(Integer maxTeams) { this.maxTeams = maxTeams; }
    public Integer getCurrentTeams() { return currentTeams; }
    public void setCurrentTeams(Integer currentTeams) { this.currentTeams = currentTeams; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}