package com.cybergamer.tournament_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para crear un torneo")
public class CreateTournamentDTO {

    @Schema(description = "Nombre del torneo", example = "Torneo Valorant")
    private String name;

    @Schema(description = "Juego del torneo", example = "Valorant")
    private String game;

    @Schema(description = "Número máximo de equipos", example = "8")
    private Integer maxTeams;

    @Schema(description = "ID del usuario organizador", example = "1")
    private Long userId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGame() { return game; }
    public void setGame(String game) { this.game = game; }
    public Integer getMaxTeams() { return maxTeams; }
    public void setMaxTeams(Integer maxTeams) { this.maxTeams = maxTeams; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}