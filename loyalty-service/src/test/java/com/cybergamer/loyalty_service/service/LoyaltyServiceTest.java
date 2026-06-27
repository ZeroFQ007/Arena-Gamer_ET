package com.cybergamer.loyalty_service.service;

import com.cybergamer.loyalty_service.entity.LoyaltyAccount;
import com.cybergamer.loyalty_service.repository.LoyaltyRepository;
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
class LoyaltyServiceTest {

    @Mock
    private LoyaltyRepository loyaltyRepository;

    @InjectMocks
    private LoyaltyService loyaltyService;

    @Test
    void getAccount_Existe_RetornaCuenta() {
        LoyaltyAccount account = new LoyaltyAccount();
        account.setUserId("user-1");
        account.setPointsBalance(100);

        when(loyaltyRepository.findByUserId("user-1")).thenReturn(Optional.of(account));

        Optional<LoyaltyAccount> result = loyaltyService.getAccount("user-1");

        assertTrue(result.isPresent());
        assertEquals(100, result.get().getPointsBalance());
    }

    @Test
    void addPoints_CuentaNueva_SubeAPlata() {
        // Arrange: Simulamos que el usuario no tiene cuenta
        when(loyaltyRepository.findByUserId("user-2")).thenReturn(Optional.empty());

        // Magia de Mockito: Le decimos que al guardar, retorne el mismo objeto que recibió
        when(loyaltyRepository.save(any(LoyaltyAccount.class))).thenAnswer(i -> i.getArgument(0));

        // Act: Agregamos 600 puntos (debería crearla y subir a PLATA)
        LoyaltyAccount result = loyaltyService.addPoints("user-2", 600);

        // Assert
        assertNotNull(result);
        assertEquals(600, result.getPointsBalance());
        assertEquals("PLATA", result.getTier());
        verify(loyaltyRepository, times(1)).save(any(LoyaltyAccount.class));
    }

    @Test
    void addPoints_CuentaExistente_SubeAOro() {
        // Arrange: El usuario ya tiene 400 puntos (BRONCE)
        LoyaltyAccount cuentaExistente = new LoyaltyAccount();
        cuentaExistente.setUserId("user-3");
        cuentaExistente.setPointsBalance(400);
        cuentaExistente.setTier("BRONCE");

        when(loyaltyRepository.findByUserId("user-3")).thenReturn(Optional.of(cuentaExistente));
        when(loyaltyRepository.save(any(LoyaltyAccount.class))).thenAnswer(i -> i.getArgument(0));

        // Act: Agregamos 600 puntos (400 + 600 = 1000 -> ORO)
        LoyaltyAccount result = loyaltyService.addPoints("user-3", 600);

        // Assert
        assertEquals(1000, result.getPointsBalance());
        assertEquals("ORO", result.getTier());
    }

    @Test
    void redeemPoints_Exito_BajaRango() {
        // Arrange: Usuario tiene 900 puntos (PLATA). Gastará 500, le quedarán 400 (BRONCE).
        LoyaltyAccount cuentaExistente = new LoyaltyAccount();
        cuentaExistente.setUserId("user-4");
        cuentaExistente.setPointsBalance(900);
        cuentaExistente.setTier("PLATA");

        when(loyaltyRepository.findByUserId("user-4")).thenReturn(Optional.of(cuentaExistente));
        when(loyaltyRepository.save(any(LoyaltyAccount.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        LoyaltyAccount result = loyaltyService.redeemPoints("user-4", 99L);

        // Assert
        assertEquals(400, result.getPointsBalance()); // 900 - 500
        assertEquals("BRONCE", result.getTier());
        verify(loyaltyRepository, times(1)).save(any(LoyaltyAccount.class));
    }

    @Test
    void redeemPoints_PuntosInsuficientes_LanzaExcepcion() {
        // Arrange: Usuario solo tiene 200 puntos (el canje cuesta 500)
        LoyaltyAccount cuentaPobre = new LoyaltyAccount();
        cuentaPobre.setUserId("user-5");
        cuentaPobre.setPointsBalance(200);

        when(loyaltyRepository.findByUserId("user-5")).thenReturn(Optional.of(cuentaPobre));

        // Act & Assert
        assertThrows(ResponseStatusException.class,
                () -> loyaltyService.redeemPoints("user-5", 99L));

        // Verificamos que no se intentó descontar nada en BD
        verify(loyaltyRepository, never()).save(any(LoyaltyAccount.class));
    }

    @Test
    void redeemPoints_SinCuenta_LanzaExcepcion() {
        // Arrange: Usuario fantasma
        when(loyaltyRepository.findByUserId("fantasma")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class,
                () -> loyaltyService.redeemPoints("fantasma", 99L));
    }
}