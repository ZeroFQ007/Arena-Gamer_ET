package com.cybergamer.tournament_service.service;

import com.cybergamer.tournament_service.client.UserClient;
import com.cybergamer.tournament_service.dto.CreateTournamentDTO;
import com.cybergamer.tournament_service.entity.Tournament;
import com.cybergamer.tournament_service.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final UserClient userClient;

    public TournamentService(TournamentRepository tournamentRepository, UserClient userClient) {
        this.tournamentRepository = tournamentRepository;
        this.userClient = userClient;
    }

    public Tournament createTournament(CreateTournamentDTO dto) {
        userClient.getUserById(dto.getUserId());

        Tournament tournament = new Tournament();
        tournament.setName(dto.getName());
        tournament.setGame(dto.getGame());
        tournament.setMaxTeams(dto.getMaxTeams());
        tournament.setCurrentTeams(0);
        tournament.setStatus("OPEN");

        return tournamentRepository.save(tournament);
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }
}
