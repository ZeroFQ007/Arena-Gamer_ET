package com.cybergamer.loyalty_service.controller;

import com.cybergamer.loyalty_service.dto.EarnPointsDTO;
import com.cybergamer.loyalty_service.dto.RedeemRequestDTO;
import com.cybergamer.loyalty_service.service.LoyaltyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/loyalty")
public class LoyaltyController {

    // Conectamos el controlador con el servicio
    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getLoyaltyProfile(@PathVariable String userId) {
        return ResponseEntity.ok("Perfil de lealtad para el usuario " + userId + ": 1500 puntos, Nivel PLATA");
    }

    @PostMapping("/redeem")
    public ResponseEntity<?> redeemPoints(@RequestBody RedeemRequestDTO request) {
        // Llamamos al nuevo método del servicio
        String result = loyaltyService.redeemPoints(request.getUserId(), request.getRewardId());

        // Si el resultado empieza con "Error", podríamos devolver un código 400 (Bad Request),
        // pero por ahora un 200 OK con el mensaje de error en texto nos sirve.
        return ResponseEntity.ok(result);
    }

    // ¡Aquí está el cambio principal!
    @PostMapping("/earn")
    public ResponseEntity<?> earnPoints(@RequestBody EarnPointsDTO request) {
        // Llamamos al método addPoints que creamos en el LoyaltyService
        String result = loyaltyService.addPoints(request.getUserId(), request.getAmount());

        return ResponseEntity.ok(result);
    }
}