package com.example.arenawallet.service;

import com.example.arenawallet.client.UserClient;
import com.example.arenawallet.client.UserResponse;
import com.example.arenawallet.dto.BilleteraCommand;
import com.example.arenawallet.dto.BilleteraResult;
import com.example.arenawallet.model.Billetera;
import com.example.arenawallet.model.BilleteraHistory; // <-- Importación agregada
import com.example.arenawallet.repository.BilleteraHistoryRepository;
import com.example.arenawallet.repository.BilleteraRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

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
        // Corregido: any(BilleteraHistory.class)
        verify(historialRepository, times(1)).save(any(BilleteraHistory.class));
    }

    @Test
    void crear_UsuarioNoExiste_LanzaExcepcion() {
        BilleteraCommand cmd = new BilleteraCommand(99L, 1000.0, 0);
        when(userClient.getUserById(99L)).thenReturn(null);

        assertThrows(ResponseStatusException.class, () -> billeteraService.crear(cmd));

        // Corregido: any(Billetera.class)
        verify(billeteraRepository, never()).save(any(Billetera.class));
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
        // Corregido: any(BilleteraHistory.class)
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

        // Corregido: any(Billetera.class)
        verify(billeteraRepository, never()).save(any(Billetera.class));
    }
}