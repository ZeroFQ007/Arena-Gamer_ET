package com.cybergamer.tournament_service.service;

import com.cybergamer.tournament_service.client.NotificationClient;
import com.cybergamer.tournament_service.client.UserClient;
import com.cybergamer.tournament_service.dto.CreateTournamentDTO;
import com.cybergamer.tournament_service.entity.Tournament;
import com.cybergamer.tournament_service.repository.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class TournamentService {

    private static final Logger log = Logger.getLogger(TournamentService.class.getName());
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
        userClient.getUserById(dto.getUserId());

        Tournament tournament = new Tournament();
        tournament.setName(dto.getName());
        tournament.setGame(dto.getGame());
        tournament.setMaxTeams(dto.getMaxTeams());
        tournament.setCurrentTeams(0);
        tournament.setStatus("OPEN");

        Tournament saved = tournamentRepository.save(tournament);

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
            log.info("Notificacion enviada por creacion de torneo: " + saved.getName());
        } catch (Exception e) {
            log.warning("Error al notificar creacion de torneo: " + e.getMessage());
        }

        return saved;
    }

    public List<Tournament> getAllTournaments() {
        return tournamentRepository.findAll();
    }
}