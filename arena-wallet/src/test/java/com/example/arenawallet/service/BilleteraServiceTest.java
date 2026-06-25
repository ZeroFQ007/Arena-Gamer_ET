package com.example.arenawallet.service;

import com.example.arenawallet.client.UserClient;
import com.example.arenawallet.client.UserResponse;
import com.example.arenawallet.dto.BilleteraCommand;
import com.example.arenawallet.dto.BilleteraResult;
import com.example.arenawallet.exception.BilleteraNotFoundException;
import com.example.arenawallet.model.Billetera;
import com.example.arenawallet.model.BilleteraHistory;
import com.example.arenawallet.repository.BilleteraHistoryRepository;
import com.example.arenawallet.repository.BilleteraRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BilleteraServiceTest {

    @Mock
    private BilleteraRepository billeteraRepository;

    @Mock
    private BilleteraHistoryRepository historialRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private BilleteraService billeteraService;

    @Test
    void listarTodas_ReturnsAll() {
        when(billeteraRepository.findAll()).thenReturn(List.of(new Billetera(), new Billetera()));

        List<BilleteraResult> result = billeteraService.listarTodas();

        assertEquals(2, result.size());
    }

    @Test
    void obtenerPorId_WhenExists_Returns() {
        Billetera b = new Billetera(1L, 10L, 5000.0, 100);
        when(billeteraRepository.findById(1L)).thenReturn(Optional.of(b));

        BilleteraResult result = billeteraService.obtenerPorId(1L);

        assertEquals(1L, result.id());
        assertEquals(5000.0, result.saldo());
    }

    @Test
    void obtenerPorId_WhenNotExists_Throws() {
        when(billeteraRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BilleteraNotFoundException.class, () -> billeteraService.obtenerPorId(99L));
    }

    @Test
    void crear_Exito() {
        BilleteraCommand cmd = new BilleteraCommand(1L, 1000.0, 0);
        UserResponse mockUser = new UserResponse(1L, "gamer1", "gamer@test.com", "USER", true);

        when(userClient.getUserById(1L)).thenReturn(mockUser);
        when(billeteraRepository.existsByIdUsuario(1L)).thenReturn(false);

        Billetera guardada = new Billetera();
        guardada.setId(100L);
        guardada.setIdUsuario(1L);
        guardada.setSaldo(1000.0);
        guardada.setPuntosFidelizacion(0);
        when(billeteraRepository.save(any(Billetera.class))).thenReturn(guardada);

        BilleteraResult result = billeteraService.crear(cmd);

        assertNotNull(result);
        assertEquals(100L, result.id());
        assertEquals(1000.0, result.saldo());
        verify(historialRepository, times(1)).save(any(BilleteraHistory.class));
    }

    @Test
    void crear_UsuarioNoExiste_LanzaExcepcion() {
        BilleteraCommand cmd = new BilleteraCommand(99L, 1000.0, 0);
        when(userClient.getUserById(99L)).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> billeteraService.crear(cmd));
        verify(billeteraRepository, never()).save(any(Billetera.class));
    }

    @Test
    void crear_IdUsuarioDuplicado_LanzaExcepcion() {
        BilleteraCommand cmd = new BilleteraCommand(1L, 1000.0, 0);
        when(userClient.getUserById(1L)).thenReturn(new UserResponse(1L, "gamer1", "gamer@test.com", "USER", true));
        when(billeteraRepository.existsByIdUsuario(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> billeteraService.crear(cmd));
        verify(billeteraRepository, never()).save(any(Billetera.class));
    }

    @Test
    void actualizar_WhenExists_Updates() {
        Billetera existing = new Billetera(1L, 10L, 5000.0, 100);
        when(billeteraRepository.findById(1L)).thenReturn(Optional.of(existing));

        BilleteraCommand cmd = new BilleteraCommand(10L, 8000.0, 200);
        when(billeteraRepository.save(any(Billetera.class))).thenReturn(existing);

        BilleteraResult result = billeteraService.actualizar(1L, cmd);

        assertEquals(8000.0, result.saldo());
        assertEquals(200, result.puntosFidelizacion());
    }

    @Test
    void recargarSaldo_WithValidAmount_Updates() {
        Billetera b = new Billetera(1L, 10L, 5000.0, 100);
        when(billeteraRepository.findById(1L)).thenReturn(Optional.of(b));
        when(billeteraRepository.save(any(Billetera.class))).thenReturn(b);

        BilleteraResult result = billeteraService.recargarSaldo(1L, 2000.0);

        assertEquals(7000.0, result.saldo());
        verify(historialRepository).save(any(BilleteraHistory.class));
    }

    @Test
    void recargarSaldo_WithNullMonto_Throws() {
        assertThrows(IllegalArgumentException.class, () -> billeteraService.recargarSaldo(1L, null));
    }

    @Test
    void recargarSaldo_WithNegativeMonto_Throws() {
        assertThrows(IllegalArgumentException.class, () -> billeteraService.recargarSaldo(1L, -100.0));
    }

    @Test
    void descontarSaldo_Exito() {
        Long idBilletera = 1L;
        Billetera billeteraMock = new Billetera();
        billeteraMock.setId(idBilletera);
        billeteraMock.setSaldo(5000.0);

        when(billeteraRepository.findById(idBilletera)).thenReturn(Optional.of(billeteraMock));
        when(billeteraRepository.save(any(Billetera.class))).thenReturn(billeteraMock);

        BilleteraResult result = billeteraService.descontarSaldo(idBilletera, 2000.0);

        assertEquals(3000.0, result.saldo());
        verify(historialRepository, times(1)).save(any(BilleteraHistory.class));
    }

    @Test
    void descontarSaldo_SaldoInsuficiente_LanzaExcepcion() {
        Long idBilletera = 1L;
        Billetera billeteraMock = new Billetera();
        billeteraMock.setId(idBilletera);
        billeteraMock.setSaldo(1000.0);

        when(billeteraRepository.findById(idBilletera)).thenReturn(Optional.of(billeteraMock));

        assertThrows(ResponseStatusException.class,
                () -> billeteraService.descontarSaldo(idBilletera, 3000.0));
        verify(billeteraRepository, never()).save(any(Billetera.class));
    }

    @Test
    void descontarSaldo_WithNullMonto_Throws() {
        assertThrows(IllegalArgumentException.class, () -> billeteraService.descontarSaldo(1L, null));
    }

    @Test
    void obtenerHistorial_WhenExists_Returns() {
        when(billeteraRepository.existsById(1L)).thenReturn(true);
        when(historialRepository.findByBilleteraIdOrderByFechaOperacionDesc(1L))
                .thenReturn(List.of(new BilleteraHistory()));

        List<BilleteraHistory> result = billeteraService.obtenerHistorial(1L);

        assertEquals(1, result.size());
    }

    @Test
    void obtenerHistorial_WhenNotExists_Throws() {
        when(billeteraRepository.existsById(99L)).thenReturn(false);

        assertThrows(BilleteraNotFoundException.class,
                () -> billeteraService.obtenerHistorial(99L));
    }

    @Test
    void eliminar_WhenExists_Deletes() {
        when(billeteraRepository.existsById(1L)).thenReturn(true);

        billeteraService.eliminar(1L);

        verify(billeteraRepository).deleteById(1L);
    }

    @Test
    void eliminar_WhenNotExists_Throws() {
        when(billeteraRepository.existsById(99L)).thenReturn(false);

        assertThrows(BilleteraNotFoundException.class, () -> billeteraService.eliminar(99L));
        verify(billeteraRepository, never()).deleteById(any());
    }
}
