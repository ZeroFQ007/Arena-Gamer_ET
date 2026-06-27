package com.example.sessionservice.service;

import com.example.sessionservice.client.LoyaltyClient;
import com.example.sessionservice.client.StationClient;
import com.example.sessionservice.client.StationResponse;
import com.example.sessionservice.client.UserClient;
import com.example.sessionservice.client.WalletClient;
import com.example.sessionservice.model.Session;
import com.example.sessionservice.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private StationClient stationClient;
    @Mock
    private UserClient userClient;
    @Mock
    private WalletClient walletClient;
    @Mock
    private LoyaltyClient loyaltyClient;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void startSession_Exito() {
        // Arrange
        Session nuevaSesion = new Session();
        nuevaSesion.setUserId(1L);
        nuevaSesion.setStationId(10L);

        // 1. El usuario existe
        when(userClient.existsUser(1L)).thenReturn(true);

        // 2. La estación existe y está disponible
        StationResponse mockStation = mock(StationResponse.class);
        when(mockStation.available()).thenReturn(true);
        when(mockStation.name()).thenReturn("PC Gamer 1");
        when(stationClient.findById(10L)).thenReturn(Optional.of(mockStation));

        // 3. No hay sesiones activas previas
        when(sessionRepository.findByStationIdAndStatus(10L, Session.SessionStatus.ACTIVE))
                .thenReturn(Optional.empty());
        when(sessionRepository.findByUserIdAndStatus(1L, Session.SessionStatus.ACTIVE))
                .thenReturn(Optional.empty());

        // 4. Guardado de la sesión
        Session sesionGuardada = new Session();
        sesionGuardada.setId(100L);
        sesionGuardada.setUserId(1L);
        sesionGuardada.setStationId(10L);
        sesionGuardada.setStatus(Session.SessionStatus.ACTIVE);
        when(sessionRepository.save(any(Session.class))).thenReturn(sesionGuardada);

        // Act
        Session result = sessionService.startSession(nuevaSesion);

        // Assert
        assertNotNull(result);
        assertEquals(Session.SessionStatus.ACTIVE, result.getStatus());
        assertNotNull(nuevaSesion.getStartTime()); // Verificamos que se le asignó fecha de inicio
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    void startSession_UsuarioNoExiste_LanzaExcepcion() {
        // Arrange
        Session nuevaSesion = new Session();
        nuevaSesion.setUserId(99L); // Usuario inventado

        when(userClient.existsUser(99L)).thenReturn(false);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> sessionService.startSession(nuevaSesion));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void startSession_EstacionOcupada_LanzaExcepcion() {
        // Arrange
        Session nuevaSesion = new Session();
        nuevaSesion.setUserId(1L);
        nuevaSesion.setStationId(10L);

        when(userClient.existsUser(1L)).thenReturn(true);

        // Simulamos que la estación existe pero NO está disponible
        StationResponse mockStation = mock(StationResponse.class);
        when(mockStation.available()).thenReturn(false);
        when(stationClient.findById(10L)).thenReturn(Optional.of(mockStation));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> sessionService.startSession(nuevaSesion));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void finishSession_Exito_CobraYDaPuntos() {
        // Arrange
        Long sessionId = 100L;
        Session sesionActiva = new Session();
        sesionActiva.setId(sessionId);
        sesionActiva.setUserId(1L);
        sesionActiva.setStatus(Session.SessionStatus.ACTIVE);
        sesionActiva.setDurationMinutes(60); // 1 hora de juego

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(sesionActiva));
        when(walletClient.cobrarSesion(eq(1L), anyDouble())).thenReturn(true);
        when(sessionRepository.save(any(Session.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Session result = sessionService.finishSession(sessionId);

        // Assert
        assertEquals(Session.SessionStatus.FINISHED, result.getStatus());
        assertNotNull(result.getEndTime());

        // Verificamos que se haya cobrado correctamente (60 min * $10 = $600)
        verify(walletClient, times(1)).cobrarSesion(1L, 600.0);
        // Verificamos que se den los puntos de lealtad
        verify(loyaltyClient, times(1)).acreditarPuntos(1L, 60);
        verify(sessionRepository, times(1)).save(any(Session.class));
    }

    @Test
    void finishSession_NoActiva_LanzaExcepcion() {
        // Arrange
        Session sesionFinalizada = new Session();
        sesionFinalizada.setId(100L);
        sesionFinalizada.setStatus(Session.SessionStatus.FINISHED);

        when(sessionRepository.findById(100L)).thenReturn(Optional.of(sesionFinalizada));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> sessionService.finishSession(100L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(walletClient, never()).cobrarSesion(anyLong(), anyDouble());
    }
}