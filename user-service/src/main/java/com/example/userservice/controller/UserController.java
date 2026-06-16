package com.example.userservice.controller;

import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios", description = "Operaciones para gestionar usuarios de Arena Gamer")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios registrados")
    @ApiResponse(responseCode = "200", description = "Usuarios obtenidos correctamente")
    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "Buscar usuario por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Crear usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "409", description = "Username o email ya existe")
    })
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        User creado = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> update(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.update(id, user));
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar usuarios por rol")
    @ApiResponse(responseCode = "200", description = "Usuarios obtenidos correctamente")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getByRole(
            @Parameter(description = "Rol del usuario: PLAYER o STAFF", example = "PLAYER")
            @PathVariable User.Role role) {
        return ResponseEntity.ok(userService.findByRole(role));
    }

    @Operation(summary = "Listar usuarios activos")
    @ApiResponse(responseCode = "200", description = "Usuarios activos obtenidos correctamente")
    @GetMapping("/active")
    public ResponseEntity<List<User>> getActivos() {
        return ResponseEntity.ok(userService.findActivos());
    }
}