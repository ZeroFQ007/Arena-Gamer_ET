package com.cybergamer.tournament_service.dto;

public class CreateTournamentDTO {
    private String name;
    private String game;
    private Integer maxTeams;
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