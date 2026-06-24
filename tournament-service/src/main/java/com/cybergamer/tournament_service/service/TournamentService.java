package com.cybergamer.tournament_service.service;

import com.cybergamer.tournament_service.client.NotificationClient;
import com.cybergamer.tournament_service.client.UserClient;
import com.cybergamer.tournament_service.dto.CreateTournamentDTO;
import com.cybergamer.tournament_service.entity.Tournament;
import com.cybergamer.tournament_service.repository.TournamentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class TournamentService {

    private static final Logger log = LoggerFactory.getLogger(TournamentService.class);

    private final TournamentRepository tournamentRepository;
    private final UserClient userClient;
    private final NotificationClient notificationClient;

    public TournamentService(TournamentRepository tournamentRepository,
                             UserClient userClient,
                             NotificationClient notificationClient) {
        this.tournamentRepository = tournamentRepository;
        this.userClient = userClient;
        this.notificationClient = notificationClient;
    }

    public Tournament createTournament(CreateTournamentDTO dto) {
        log.info("[TOURNAMENT] Verificando usuario id={} antes de crear torneo", dto.getUserId());
        userClient.getUserById(dto.getUserId());

        Tournament tournament = new Tournament();
        tournament.setName(dto.getName());
        tournament.setGame(dto.getGame());
        tournament.setMaxTeams(dto.getMaxTeams());
        tournament.setCurrentTeams(0);
        tournament.setStatus("OPEN");

        Tournament saved = tournamentRepository.save(tournament);
        log.info("[TOURNAMENT] Torneo '{}' creado con id={}", saved.getName(), saved.getId());

        try {
            String mensaje = String.format(
                    "Nuevo torneo creado: '%s' - Juego: %s - Equipos maximos: %d",
                    saved.getName(), saved.getGame(), saved.getMaxTeams()
            );
            notificationClient.sendNotification(Map.of(
                    "recipient", "jugadores@arenagamer.cl",
                    "message", mensaje,
                    "channel", "EMAIL"
            ));
            log.info("[TOURNAMENT] Notificacion enviada por creacion de torneo '{}'", saved.getName());
        } catch (Exception e) {
            log.warn("[TOURNAMENT] No se pudo notificar creacion del torneo '{}': {}", saved.getName(), e.getMessage());
        }

        return saved;
    }

    public Tournament findById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Torneo con id " + id + " no encontrado"));
    }

    public List<Tournament> getAllTournaments() {
        List<Tournament> torneos = tournamentRepository.findAll();
        log.info("[TOURNAMENT] Consultando lista de torneos — total: {}", torneos.size());
        return torneos;
    }
}
