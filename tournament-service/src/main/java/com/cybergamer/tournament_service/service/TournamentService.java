package com.cybergamer.tournament_service.service;

import com.cybergamer.tournament_service.dto.CreateTournamentDTO;
import com.cybergamer.tournament_service.entity.Tournament;
import com.cybergamer.tournament_service.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    public TournamentService(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    // Método para crear un torneo nuevo
    public Tournament createTournament(CreateTournamentDTO dto) {
        Tournament tournament = new Tournament();
        tournament.setName(dto.getName());
        tournament.setGame(dto.getGame());
        tournament.setMaxTeams(dto.getMaxTeams());

        // Valores por defecto al crear
        tournament.setCurrentTeams(0);
        tournament.setStatus("OPEN");

        return tournamentRepository.save(tournament);
    }

    // Método para ver todos los torneos disponibles
    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }
}