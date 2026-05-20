package com.example.sessionservice.service;

import com.example.sessionservice.client.LoyaltyClient;
import com.example.sessionservice.client.StationClient;
import com.example.sessionservice.client.StationResponse;
import com.example.sessionservice.client.UserClient;
import com.example.sessionservice.client.UserResponse;
import com.example.sessionservice.client.WalletClient;
import com.example.sessionservice.dto.SessionResponse;
import com.example.sessionservice.model.Session;
import com.example.sessionservice.repository.SessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SessionService {

    private static final double TARIFA_POR_MINUTO = 10.0;

    private final SessionRepository sessionRepository;
    private final StationClient stationClient;
    private final UserClient userClient;
    private final WalletClient walletClient;
    private final LoyaltyClient loyaltyClient;

    public SessionService(SessionRepository sessionRepository,
                          StationClient stationClient,
                          UserClient userClient,
                          WalletClient walletClient,
                          LoyaltyClient loyaltyClient) {
        this.sessionRepository = sessionRepository;
        this.stationClient = stationClient;
        this.userClient = userClient;
        this.walletClient = walletClient;
        this.loyaltyClient = loyaltyClient;
    }

    public SessionResponse toResponse(Session session) {
        String username = userClient.findById(session.getUserId())
                .map(UserResponse::username)
                .orElse("Desconocido");

        String stationName = stationClient.findById(session.getStationId())
                .map(StationResponse::name)
                .orElse("Desconocida");

        return new SessionResponse(
                session.getId(),
                session.getUserId(),
                username,
                session.getStationId(),
                stationName,
                session.getStartTime(),
                session.getEndTime(),
                session.getStatus().name(),
                session.getDurationMinutes()
        );
    }

    public List<SessionResponse> findAllWithDetails() {
        return sessionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public SessionResponse findByIdWithDetails(Long id) {
        return toResponse(findById(id));
    }

    public List<Session> findAll() {
        return sessionRepository.findAll();
    }

    public Session findById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Sesión no encontrada con id: " + id));
    }

    public Session startSession(Session session) {
        log.info("[SESSION] Verificando existencia de usuario id={}", session.getUserId());
        if (!userClient.existsUser(session.getUserId())) {
            log.warn("[SESSION] Usuario id={} no encontrado en user-service", session.getUserId());
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Usuario con id " + session.getUserId() + " no existe");
        }

        log.info("[SESSION] Verificando disponibilidad de estación id={}", session.getStationId());
        stationClient.findById(session.getStationId()).ifPresentOrElse(
                station -> {
                    if (!station.available()) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "La estación id=" + session.getStationId() + " no está disponible");
                    }
                    log.info("[SESSION] Estación '{}' disponible, procediendo a iniciar sesión", station.name());
                },
                () -> {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Estación con id " + session.getStationId() + " no existe");
                }
        );

        sessionRepository.findByStationIdAndStatus(session.getStationId(), Session.SessionStatus.ACTIVE)
                .ifPresent(s -> { throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "La estación ya tiene una sesión activa"); });

        sessionRepository.findByUserIdAndStatus(session.getUserId(), Session.SessionStatus.ACTIVE)
                .ifPresent(s -> { throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "El usuario ya tiene una sesión activa"); });

        session.setStartTime(LocalDateTime.now());
        session.setStatus(Session.SessionStatus.ACTIVE);
        Session saved = sessionRepository.save(session);
        log.info("[SESSION] Sesión id={} iniciada — usuario={} en estación={}",
                saved.getId(), saved.getUserId(), saved.getStationId());
        return saved;
    }

    public Session finishSession(Long id) {
        Session session = findById(id);
        if (session.getStatus() != Session.SessionStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La sesión no está activa");
        }

        session.setEndTime(LocalDateTime.now());
        session.setStatus(Session.SessionStatus.FINISHED);

        int duracion = session.getDurationMinutes() != null ? session.getDurationMinutes() : 30;
        session.setDurationMinutes(duracion);

        Session saved = sessionRepository.save(session);
        log.info("[SESSION] Sesión id={} finalizada. Duración: {} min", id, duracion);

        double costo = duracion * TARIFA_POR_MINUTO;
        log.info("[SESSION] Iniciando cobro de ${} al usuario id={}", costo, session.getUserId());
        boolean cobrado = walletClient.cobrarSesion(session.getUserId(), costo);
        if (!cobrado) {
            log.warn("[SESSION] No se pudo cobrar sesión al usuario id={}. Sesión igual queda FINISHED.", session.getUserId());
        }

        loyaltyClient.acreditarPuntos(session.getUserId(), duracion);

        return saved;
    }

    public Session cancelSession(Long id) {
        Session session = findById(id);
        if (session.getStatus() != Session.SessionStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La sesión no está activa");
        }
        session.setEndTime(LocalDateTime.now());
        session.setStatus(Session.SessionStatus.CANCELLED);
        Session saved = sessionRepository.save(session);
        log.info("[SESSION] Sesión id={} cancelada para usuario id={}", id, session.getUserId());
        return saved;
    }

    public List<Session> findByUserId(Long userId) {
        return sessionRepository.findByUserId(userId);
    }

    public List<Session> findByStationId(Long stationId) {
        return sessionRepository.findByStationId(stationId);
    }

    public List<Session> findActivas() {
        return sessionRepository.findByStatus(Session.SessionStatus.ACTIVE);
    }
}