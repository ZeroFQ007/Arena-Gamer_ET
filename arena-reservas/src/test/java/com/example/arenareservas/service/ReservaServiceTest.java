package com.example.arenareservas.service;

import com.example.arenareservas.client.InventoryClient;
import com.example.arenareservas.dto.ReservaCommand;
import com.example.arenareservas.dto.ReservaResult;
import com.example.arenareservas.exception.ReservaNotFoundException;
import com.example.arenareservas.model.Reserva;
import com.example.arenareservas.model.ReservaHistory;
import com.example.arenareservas.repository.ReservaHistoryRepository;
import com.example.arenareservas.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private ReservaHistoryRepository historialRepository;
    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void crear_Exito() {
        // Arrange (Usamos una fecha futura para que pase la validación)
        LocalDate fechaFutura = LocalDate.now().plusDays(5);
        ReservaCommand cmd = new ReservaCommand(1L, 10L, fechaFutura, "10:00-12:00");

        // Simulamos que NO hay conflicto de horario
        when(reservaRepository.existsByEstacionIdAndFechaAndBloqueHorarioAndEstadoNotAndIdNot(
                eq(10L), eq(fechaFutura), eq("10:00-12:00"), eq("CANCELADA"), eq(-1L)
        )).thenReturn(false);

        Reserva reservaGuardada = new Reserva();
        reservaGuardada.setId(100L);
        reservaGuardada.setUsuarioId(1L);
        reservaGuardada.setEstacionId(10L);
        reservaGuardada.setFecha(fechaFutura);
        reservaGuardada.setBloqueHorario("10:00-12:00");
        reservaGuardada.setEstado("NUEVA");

        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaGuardada);

        // Act
        ReservaResult result = reservaService.crear(cmd);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals("NUEVA", result.estado());
        verify(reservaRepository, times(1)).save(any(Reserva.class));
        verify(historialRepository, times(1)).save(any(ReservaHistory.class));
    }

    @Test
    void crear_FechaPasada_LanzaExcepcion() {
        // Arrange (Usamos fecha de ayer)
        LocalDate fechaPasada = LocalDate.now().minusDays(1);
        ReservaCommand cmd = new ReservaCommand(1L, 10L, fechaPasada, "10:00-12:00");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> reservaService.crear(cmd));

        // Verificamos que JAMÁS llegó a consultar conflictos ni a guardar nada
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void crear_ConflictoHorario_LanzaExcepcion() {
        // Arrange
        LocalDate fechaFutura = LocalDate.now().plusDays(2);
        ReservaCommand cmd = new ReservaCommand(1L, 10L, fechaFutura, "15:00-17:00");

        // Simulamos que SÍ existe un conflicto
        when(reservaRepository.existsByEstacionIdAndFechaAndBloqueHorarioAndEstadoNotAndIdNot(
                eq(10L), eq(fechaFutura), eq("15:00-17:00"), eq("CANCELADA"), eq(-1L)
        )).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> reservaService.crear(cmd));
        verify(reservaRepository, never()).save(any());
    }

    @Test
    void cambiarEstado_Confirmada_LlamaAInventoryClient() {
        // Arrange
        Long idReserva = 1L;
        Reserva reservaMock = new Reserva();
        reservaMock.setId(idReserva);
        reservaMock.setEstacionId(10L); // Asumimos productoId = 10L
        reservaMock.setEstado("NUEVA");

        when(reservaRepository.findById(idReserva)).thenReturn(Optional.of(reservaMock));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaMock);

        // Act
        ReservaResult result = reservaService.cambiarEstado(idReserva, "CONFIRMADA", "Pago realizado");

        // Assert
        assertEquals("CONFIRMADA", result.estado());
        verify(historialRepository, times(1)).save(any(ReservaHistory.class));
        // Verificamos que efectivamente se llamó al microservicio de inventario
        verify(inventoryClient, times(1)).actualizarStock(eq(10L), anyMap());
    }

    @Test
    void cambiarEstado_ReservaNoExiste_LanzaExcepcion() {
        // Arrange
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReservaNotFoundException.class,
                () -> reservaService.cambiarEstado(99L, "CANCELADA", "Me arrepentí"));
    }
}