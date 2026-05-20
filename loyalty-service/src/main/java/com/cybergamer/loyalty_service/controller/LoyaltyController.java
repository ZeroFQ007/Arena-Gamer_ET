package com.cybergamer.loyalty_service.controller;

import com.cybergamer.loyalty_service.dto.EarnPointsDTO;
import com.cybergamer.loyalty_service.dto.RedeemRequestDTO;
import com.cybergamer.loyalty_service.entity.LoyaltyAccount;
import com.cybergamer.loyalty_service.service.LoyaltyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/loyalty")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getLoyaltyProfile(@PathVariable String userId) {
        Optional<LoyaltyAccount> account = loyaltyService.getAccount(userId);

        if (account.isEmpty()) {
            return ResponseEntity.ok("Usuario " + userId + " no tiene cuenta de lealtad");
        }

        LoyaltyAccount a = account.get();
        return ResponseEntity.ok("Perfil de lealtad para el usuario " + userId +
                ": " + a.getPointsBalance() + " puntos, Nivel " + a.getTier());
    }

    @PostMapping("/redeem")
    public ResponseEntity<?> redeemPoints(@RequestBody RedeemRequestDTO request) {
        String result = loyaltyService.redeemPoints(request.getUserId(), request.getRewardId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/earn")
    public ResponseEntity<?> earnPoints(@RequestBody EarnPointsDTO request) {
        String result = loyaltyService.addPoints(request.getUserId(), request.getAmount());
        return ResponseEntity.ok(result);
    }
}