package com.cybergamer.loyalty_service.service;

import com.cybergamer.loyalty_service.entity.LoyaltyAccount;
import com.cybergamer.loyalty_service.repository.LoyaltyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Optional;

@Slf4j
@Service
public class LoyaltyService {

    private final LoyaltyRepository loyaltyRepository;

    public LoyaltyService(LoyaltyRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
    }

    public Optional<LoyaltyAccount> getAccount(String userId) {
        Optional<LoyaltyAccount> account = loyaltyRepository.findByUserId(userId);
        if (account.isPresent()) {
            log.info("Cuenta de lealtad encontrada: userId={}, puntos={}, tier={}",
                    userId, account.get().getPointsBalance(), account.get().getTier());
        } else {
            log.info("Cuenta de lealtad no encontrada para userId={}", userId);
        }
        return account;
    }

    public LoyaltyAccount addPoints(String userId, Integer amount) {
        Optional<LoyaltyAccount> existingAccount = loyaltyRepository.findByUserId(userId);

        LoyaltyAccount account;

        if (existingAccount.isPresent()) {
            account = existingAccount.get();
            log.info("Cuenta existente para userId={}: puntos actuales={}", userId, account.getPointsBalance());
        } else {
            account = new LoyaltyAccount();
            account.setUserId(userId);
            account.setPointsBalance(0);
            account.setTier("BRONCE");
            log.info("Nueva cuenta de lealtad creada para userId={}", userId);
        }

        int newBalance = account.getPointsBalance() + amount;
        account.setPointsBalance(newBalance);

        if (newBalance >= 1000) {
            account.setTier("ORO");
        } else if (newBalance >= 500) {
            account.setTier("PLATA");
        }

        LoyaltyAccount saved = loyaltyRepository.save(account);
        log.info("Puntos agregados: userId={}, cantidad={}, nuevo balance={}, tier={}",
                userId, amount, newBalance, saved.getTier());
        return saved;
    }

    public LoyaltyAccount redeemPoints(String userId, Long rewardId) {
        Optional<LoyaltyAccount> accountOpt = loyaltyRepository.findByUserId(userId);

        if (accountOpt.isEmpty()) {
            log.warn("Intento de canje para usuario sin cuenta de lealtad: userId={}", userId);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "El usuario no existe o no tiene puntos acumulados");
        }

        LoyaltyAccount account = accountOpt.get();
        int cost = 500;

        if (account.getPointsBalance() < cost) {
            log.warn("Puntos insuficientes para canje: userId={}, tiene={}, necesita={}",
                    userId, account.getPointsBalance(), cost);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Puntos insuficientes. Tienes " + account.getPointsBalance() + " y necesitas " + cost);
        }

        int newBalance = account.getPointsBalance() - cost;
        account.setPointsBalance(newBalance);

        if (newBalance < 500) {
            account.setTier("BRONCE");
        } else if (newBalance < 1000) {
            account.setTier("PLATA");
        }

        LoyaltyAccount saved = loyaltyRepository.save(account);
        log.info("Puntos canjeados: userId={}, rewardId={}, costo={}, nuevo balance={}, tier={}",
                userId, rewardId, cost, newBalance, saved.getTier());
        return saved;
    }
}