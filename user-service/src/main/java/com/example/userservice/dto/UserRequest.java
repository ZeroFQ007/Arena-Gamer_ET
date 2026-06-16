package com.example.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Datos para crear o actualizar un usuario")
public class UserRequest {

    @Schema(description = "Nombre de usuario único", example = "Fabry27")
    @NotBlank(message = "El username no puede estar vacío")
    private String username;

    @Schema(description = "Email del usuario", example = "fabry27@gmail.com")
    @Email(message = "Debe ser un email válido")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    @Schema(description = "Rol del usuario", example = "PLAYER", allowableValues = {"PLAYER", "STAFF"})
    @NotBlank(message = "El rol no puede estar vacío")
    private String role;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}