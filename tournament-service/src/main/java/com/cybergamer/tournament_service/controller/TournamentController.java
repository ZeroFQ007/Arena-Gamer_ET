package com.cybergamer.tournament_service.controller;

import com.cybergamer.tournament_service.dto.CreateTournamentDTO;
import com.cybergamer.tournament_service.entity.Tournament;
import com.cybergamer.tournament_service.service.TournamentLinkAssembler;
import com.cybergamer.tournament_service.service.TournamentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Torneos", description = "Operaciones para gestionar torneos en Arena Gamer")
@RestController
@RequestMapping("/api/v1/tournaments")
public class TournamentController {

    private final TournamentService tournamentService;
    private final TournamentLinkAssembler linkAssembler;

    public TournamentController(TournamentService tournamentService,
                                 TournamentLinkAssembler linkAssembler) {
        this.tournamentService = tournamentService;
        this.linkAssembler = linkAssembler;
    }

    @Operation(summary = "Crear torneo",
               description = "Crea un torneo verificando el usuario en user-service y notificando a notification-service con fallback")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Torneo creado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado en user-service")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Tournament>> createTournament(
            @RequestBody CreateTournamentDTO request) {
        Tournament nuevo = tournamentService.createTournament(request);
        return ResponseEntity.ok(linkAssembler.toModel(nuevo));
    }

    @Operation(summary = "Listar torneos",
               description = "Obtiene todos los torneos registrados con enlaces HATEOAS")
    @ApiResponse(responseCode = "200", description = "Torneos obtenidos correctamente")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Tournament>>> getTournaments() {
        List<EntityModel<Tournament>> modelos = tournamentService.getAllTournaments()
                .stream()
                .map(linkAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Tournament>> collection = CollectionModel.of(
                modelos,
                linkTo(methodOn(TournamentController.class).getTournaments()).withSelfRel()
        );

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Buscar torneo por ID",
               description = "Obtiene un torneo específico por su ID con enlaces HATEOAS")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Torneo encontrado"),
            @ApiResponse(responseCode = "404", description = "Torneo no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Tournament>> getTournamentById(
            @Parameter(description = "ID del torneo", example = "1")
            @PathVariable Long id) {
        Tournament torneo = tournamentService.findById(id);
        return ResponseEntity.ok(linkAssembler.toModel(torneo));
    }
}
