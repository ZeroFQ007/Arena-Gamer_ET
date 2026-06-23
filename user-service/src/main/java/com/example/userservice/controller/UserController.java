package com.example.userservice.controller;

import com.example.userservice.model.User;
import com.example.userservice.service.UserLinkAssembler;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Usuarios", description = "Operaciones para gestionar usuarios de Arena Gamer")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserLinkAssembler userLinkAssembler;

    public UserController(UserService userService, UserLinkAssembler userLinkAssembler) {
        this.userService = userService;
        this.userLinkAssembler = userLinkAssembler;
    }

    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios registrados con enlaces HATEOAS en _links")
    @ApiResponse(responseCode = "200", description = "Usuarios obtenidos correctamente")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<User>>> getAll() {
        List<EntityModel<User>> users = userService.findAll().stream()
                .map(userLinkAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<User>> collection = CollectionModel.of(users);
        collection.add(linkTo(methodOn(UserController.class).getAll()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @Operation(summary = "Buscar usuario por id", description = "Devuelve el usuario con enlaces HATEOAS en _links")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<User>> getById(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(userLinkAssembler.toModel(user));
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