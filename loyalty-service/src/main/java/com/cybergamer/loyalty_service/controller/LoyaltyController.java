package com.cybergamer.loyalty_service.controller;

import com.cybergamer.loyalty_service.dto.EarnPointsDTO;
import com.cybergamer.loyalty_service.dto.RedeemRequestDTO;
import com.cybergamer.loyalty_service.entity.LoyaltyAccount;
import com.cybergamer.loyalty_service.service.LoyaltyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Lealtad", description = "Operaciones para gestionar puntos de fidelización en Arena Gamer")
@RestController
@RequestMapping("/api/v1/loyalty")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @Operation(summary = "Ver perfil de lealtad", description = "Obtiene los puntos y nivel de fidelización de un usuario")
    @ApiResponse(responseCode = "200", description = "Perfil obtenido correctamente")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getLoyaltyProfile(
            @Parameter(description = "ID del usuario", example = "2")
            @PathVariable String userId) {
        Optional<LoyaltyAccount> account = loyaltyService.getAccount(userId);

        if (account.isEmpty()) {
            return ResponseEntity.ok("Usuario " + userId + " no tiene cuenta de lealtad");
        }

        LoyaltyAccount a = account.get();
        return ResponseEntity.ok("Perfil de lealtad para el usuario " + userId +
                ": " + a.getPointsBalance() + " puntos, Nivel " + a.getTier());
    }

    @Operation(summary = "Canjear puntos", description = "Canjea 500 puntos por un premio")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canje exitoso o mensaje de error de negocio")
    })
    @PostMapping("/redeem")
    public ResponseEntity<?> redeemPoints(@RequestBody RedeemRequestDTO request) {
        String result = loyaltyService.redeemPoints(request.getUserId(), request.getRewardId());
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Acreditar puntos", description = "Acredita puntos a un usuario. Crea la cuenta automáticamente si no existe")
    @ApiResponse(responseCode = "200", description = "Puntos acreditados correctamente")
    @PostMapping("/earn")
    public ResponseEntity<?> earnPoints(@RequestBody EarnPointsDTO request) {
        String result = loyaltyService.addPoints(request.getUserId(), request.getAmount());
        return ResponseEntity.ok(result);
    }
}