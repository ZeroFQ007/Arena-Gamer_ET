package com.cybergamer.tournament_service.controller;

import com.cybergamer.tournament_service.dto.CreateTournamentDTO;
import com.cybergamer.tournament_service.entity.Tournament;
import com.cybergamer.tournament_service.service.TournamentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    // Endpoint para crear torneo (POST)
    @PostMapping
    public ResponseEntity<Tournament> createTournament(@RequestBody CreateTournamentDTO request) {
        Tournament newTournament = tournamentService.createTournament(request);
        return ResponseEntity.ok(newTournament);
    }

    // Endpoint para listar todos los torneos (GET)
    @GetMapping
    public ResponseEntity<List<Tournament>> getTournaments() {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }
}