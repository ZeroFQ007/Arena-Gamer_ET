package com.example.userservice.controller;

import com.example.userservice.model.User;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        User creado = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id,
                                       @Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.update(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getByRole(@PathVariable User.Role role) {
        return ResponseEntity.ok(userService.findByRole(role));
    }

    @GetMapping("/active")
    public ResponseEntity<List<User>> getActivos() {
        return ResponseEntity.ok(userService.findActivos());
    }
}