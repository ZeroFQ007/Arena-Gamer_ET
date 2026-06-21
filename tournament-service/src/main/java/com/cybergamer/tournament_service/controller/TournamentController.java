package com.cybergamer.tournament_service.controller;

import com.cybergamer.tournament_service.dto.CreateTournamentDTO;
import com.cybergamer.tournament_service.entity.Tournament;
import com.cybergamer.tournament_service.service.TournamentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Torneos", description = "Operaciones para gestionar torneos en Arena Gamer")
@RestController
@RequestMapping("/api/v1/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;

    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @Operation(summary = "Crear torneo", description = "Crea un torneo verificando el usuario en user-service y notificando a notification-service")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Torneo creado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PostMapping
    public ResponseEntity<Tournament> createTournament(@RequestBody CreateTournamentDTO request) {
        Tournament newTournament = tournamentService.createTournament(request);
        return ResponseEntity.ok(newTournament);
    }

    @Operation(summary = "Listar torneos", description = "Obtiene todos los torneos registrados")
    @ApiResponse(responseCode = "200", description = "Torneos obtenidos correctamente")
    @GetMapping
    public ResponseEntity<List<Tournament>> getTournaments() {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }
}