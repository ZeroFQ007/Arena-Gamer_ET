package com.cybergamer.loyalty_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para acreditar puntos de lealtad a un usuario")
public class EarnPointsDTO {

    @Schema(description = "ID del usuario", example = "2")
    private String userId;

    @Schema(description = "Cantidad de puntos a acreditar", example = "500")
    private Integer amount;

    @Schema(description = "Motivo de la acreditación", example = "Sesión completada (60 min)")
    private String reason;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}