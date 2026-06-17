package com.example.arenareservas.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Schema(description = "Datos para crear o actualizar una reserva")
public record ReservaRequest(

        @Schema(description = "ID del usuario que reserva", example = "1")
        @NotNull(message = "El ID de usuario es obligatorio")
        @Min(value = 1, message = "El ID de usuario debe ser mayor a 0")
        Long usuarioId,

        @Schema(description = "ID de la estación a reservar", example = "1")
        @NotNull(message = "El ID de estación es obligatorio")
        @Min(value = 1, message = "El ID de estación debe ser mayor a 0")
        Long estacionId,

        @Schema(description = "Fecha de la reserva", example = "2026-05-25")
        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,

        @Schema(description = "Bloque horario en formato HH:mm-HH:mm", example = "14:00-15:00")
        @NotBlank(message = "El bloque horario no puede estar vacío")
        @Pattern(
                regexp = "^([01]\\d|2[0-3]):[0-5]\\d-([01]\\d|2[0-3]):[0-5]\\d$",
                message = "Formato HH:mm-HH:mm (ej: 14:00-15:00)"
        )
        String bloqueHorario
) {}