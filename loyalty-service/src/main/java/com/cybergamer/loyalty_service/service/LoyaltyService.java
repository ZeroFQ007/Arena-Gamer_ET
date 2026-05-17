package com.cybergamer.loyalty_service.service;

import com.cybergamer.loyalty_service.entity.LoyaltyAccount;
import com.cybergamer.loyalty_service.repository.LoyaltyRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoyaltyService {

    private final LoyaltyRepository loyaltyRepository;

    public LoyaltyService(LoyaltyRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
    }

    public String addPoints(String userId, Integer amount) {
        Optional<LoyaltyAccount> existingAccount = loyaltyRepository.findByUserId(userId);

        LoyaltyAccount account;

        if (existingAccount.isPresent()) {
            account = existingAccount.get();
        } else {
            account = new LoyaltyAccount();
            account.setUserId(userId);
            account.setPointsBalance(0);
            account.setTier("BRONCE");
        }

        int newBalance = account.getPointsBalance() + amount;
        account.setPointsBalance(newBalance);

        if (newBalance >= 1000) {
            account.setTier("ORO");
        } else if (newBalance >= 500) {
            account.setTier("PLATA");
        }

        loyaltyRepository.save(account);

        return "Éxito. Nuevo saldo de " + userId + ": " + newBalance + " puntos (" + account.getTier() + ")";
    }

    public String redeemPoints(String userId, Long rewardId) {
        Optional<LoyaltyAccount> accountOpt = loyaltyRepository.findByUserId(userId);

        if (accountOpt.isEmpty()) {
            return "Error: El usuario no existe o no tiene puntos acumulados.";
        }

        LoyaltyAccount account = accountOpt.get();
        int cost = 500;

        if (account.getPointsBalance() < cost) {
            return "Error: Puntos insuficientes. Tienes " + account.getPointsBalance() + " y necesitas " + cost + ".";
        }

        int newBalance = account.getPointsBalance() - cost;
        account.setPointsBalance(newBalance);

        if (newBalance < 500) {
            account.setTier("BRONCE");
        } else if (newBalance < 1000) {
            account.setTier("PLATA");
        }

        loyaltyRepository.save(account);

        return "Canje exitoso del premio ID: " + rewardId + ". Te quedan " + newBalance + " puntos (" + account.getTier() + ").";
    }
}
