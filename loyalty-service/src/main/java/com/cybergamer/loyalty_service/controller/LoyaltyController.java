package com.cybergamer.loyalty_service.controller;

import com.cybergamer.loyalty_service.dto.EarnPointsDTO;
import com.cybergamer.loyalty_service.dto.RedeemRequestDTO;
import com.cybergamer.loyalty_service.entity.LoyaltyAccount;
import com.cybergamer.loyalty_service.service.LoyaltyLinkAssembler;
import com.cybergamer.loyalty_service.service.LoyaltyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Tag(name = "Lealtad", description = "Operaciones para gestionar puntos de fidelización en Arena Gamer")
@RestController
@RequestMapping("/api/v1/loyalty")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;
    private final LoyaltyLinkAssembler loyaltyLinkAssembler;

    public LoyaltyController(LoyaltyService loyaltyService, LoyaltyLinkAssembler loyaltyLinkAssembler) {
        this.loyaltyService = loyaltyService;
        this.loyaltyLinkAssembler = loyaltyLinkAssembler;
    }

    @Operation(summary = "Ver perfil de lealtad", description = "Obtiene los puntos y nivel de fidelización de un usuario en formato JSON con enlaces HATEOAS")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "El usuario no tiene cuenta de lealtad")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<EntityModel<LoyaltyAccount>> getLoyaltyProfile(@PathVariable String userId) {
        Optional<LoyaltyAccount> account = loyaltyService.getAccount(userId);

        if (account.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuario " + userId + " no tiene cuenta de lealtad");
        }

        return ResponseEntity.ok(loyaltyLinkAssembler.toModel(account.get()));
    }

    @Operation(summary = "Canjear puntos", description = "Canjea 500 puntos por un premio y retorna la cuenta actualizada en JSON")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Canje exitoso"),
            @ApiResponse(responseCode = "400", description = "Puntos insuficientes"),
            @ApiResponse(responseCode = "404", description = "Usuario no tiene cuenta de lealtad")
    })
    @PostMapping("/redeem")
    public ResponseEntity<EntityModel<LoyaltyAccount>> redeemPoints(@RequestBody RedeemRequestDTO request) {
        LoyaltyAccount account = loyaltyService.redeemPoints(request.getUserId(), request.getRewardId());
        return ResponseEntity.ok(loyaltyLinkAssembler.toModel(account));
    }

    @Operation(summary = "Acreditar puntos", description = "Acredita puntos a un usuario y retorna la cuenta actualizada en JSON. Crea la cuenta automáticamente si no existe")
    @ApiResponse(responseCode = "200", description = "Puntos acreditados correctamente")
    @PostMapping("/earn")
    public ResponseEntity<EntityModel<LoyaltyAccount>> earnPoints(@RequestBody EarnPointsDTO request) {
        LoyaltyAccount account = loyaltyService.addPoints(request.getUserId(), request.getAmount());
        return ResponseEntity.ok(loyaltyLinkAssembler.toModel(account));
    }
}