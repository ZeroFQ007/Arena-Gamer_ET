package com.example.arenawallet.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Datos para crear o actualizar una billetera")
public record BilleteraRequest(

        @Schema(description = "ID del usuario propietario de la billetera", example = "1")
        @NotNull(message = "El ID de usuario es obligatorio")
        @Min(value = 1, message = "El ID de usuario debe ser mayor a 0")
        Long idUsuario,

        @Schema(description = "Saldo inicial de la billetera en pesos chilenos", example = "10000.0")
        @NotNull(message = "El saldo es obligatorio")
        @PositiveOrZero(message = "El saldo no puede ser negativo")
        Double saldo,

        @Schema(description = "Puntos de fidelización iniciales", example = "0")
        @NotNull(message = "Los puntos son obligatorios")
        @PositiveOrZero(message = "Los puntos no pueden ser negativos")
        Integer puntosFidelizacion
) {}