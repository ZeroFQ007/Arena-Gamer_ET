package com.example.arenainventory.dto;

import com.example.arenainventory.model.Producto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Datos para crear o actualizar un producto del inventario")
public record ProductoRequest(

        @Schema(description = "Nombre del producto", example = "PlayStation 5")
        @NotBlank(message = "El nombre del producto es obligatorio")
        String nombre,

        @Schema(description = "Categoría del producto", example = "CONSOLA")
        @NotNull(message = "La categoría es obligatoria")
        Producto.Categoria categoria,

        @Schema(description = "Cantidad disponible en stock", example = "5")
        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,

        @Schema(description = "Precio de alquiler en pesos chilenos", example = "2500.0")
        @NotNull(message = "El precio de alquiler es obligatorio")
        @Min(value = 0, message = "El precio no puede ser negativo")
        Double precioAlquiler
) {}