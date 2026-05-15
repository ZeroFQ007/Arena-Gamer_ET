package com.cybergamer.loyalty_service.service;

import com.cybergamer.loyalty_service.entity.LoyaltyAccount;
import com.cybergamer.loyalty_service.repository.LoyaltyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service // Esta anotación le dice a Spring que esta clase contiene la lógica de negocio
public class LoyaltyService {

    // Inyectamos el repositorio para poder hablar con la base de datos
    private final LoyaltyRepository loyaltyRepository;

    public LoyaltyService(LoyaltyRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
    }

    // Método para sumar puntos
    public String addPoints(String userId, Integer amount) {
        // 1. Buscamos si el jugador ya tiene una cuenta en la base de datos
        Optional<LoyaltyAccount> existingAccount = loyaltyRepository.findByUserId(userId);

        LoyaltyAccount account;

        if (existingAccount.isPresent()) {
            // Si existe, la sacamos de la caja (Optional)
            account = existingAccount.get();
        } else {
            // Si es un jugador nuevo, le creamos una cuenta desde cero
            account = new LoyaltyAccount();
            account.setUserId(userId);
            account.setPointsBalance(0);
            account.setTier("BRONCE");
        }

        // 2. Le sumamos los puntos nuevos al saldo actual
        int newBalance = account.getPointsBalance() + amount;
        account.setPointsBalance(newBalance);

        // 3. Mini lógica de niveles (Tiers)
        if (newBalance >= 1000) {
            account.setTier("ORO");
        } else if (newBalance >= 500) {
            account.setTier("PLATA");
        }

        // 4. Guardamos los cambios en la base de datos
        loyaltyRepository.save(account);

        return "Éxito. Nuevo saldo de " + userId + ": " + newBalance + " puntos (" + account.getTier() + ")";
    }
    // Método para canjear puntos (Gastar)
    public String redeemPoints(String userId, Long rewardId) {
        // 1. Buscamos al usuario en la base de datos
        Optional<LoyaltyAccount> accountOpt = loyaltyRepository.findByUserId(userId);

        if (accountOpt.isEmpty()) {
            return "Error: El usuario no existe o no tiene puntos acumulados.";
        }

        LoyaltyAccount account = accountOpt.get();
        int cost = 500; // Simulamos que el premio cuesta 500 puntos

        // 2. Validamos si tiene saldo suficiente
        if (account.getPointsBalance() < cost) {
            return "Error: Puntos insuficientes. Tienes " + account.getPointsBalance() + " y necesitas " + cost + ".";
        }

        // 3. Restamos los puntos
        int newBalance = account.getPointsBalance() - cost;
        account.setPointsBalance(newBalance);

        // 4. Actualizamos el nivel (si baja de los umbrales)
        if (newBalance < 500) {
            account.setTier("BRONCE");
        } else if (newBalance < 1000) {
            account.setTier("PLATA");
        }

        // 5. Guardamos en la base de datos
        loyaltyRepository.save(account);

        return "Canje exitoso del premio ID: " + rewardId + ". Te quedan " + newBalance + " puntos (" + account.getTier() + ").";
    }
}