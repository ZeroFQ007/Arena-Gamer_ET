package com.example.userservice.service;

import com.example.userservice.dto.SendNotificationRequest;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RestClient restClient;
    private final PasswordEncoder passwordEncoder;

    @Value("${services.notification-service.url}")
    private String notificationServiceUrl;

    public UserService(UserRepository userRepository, RestClient restClient, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.restClient = restClient;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        log.info("Obtenidos {} usuarios", users.size());
        return users;
    }

    public User findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id));
        log.info("Usuario encontrado: id={}, username={}", id, user.getUsername());
        return user;
    }

    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("Intento de crear usuario con username existente: {}", user.getUsername());
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El username ya está en uso: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            log.warn("Intento de crear usuario con email existente: {}", user.getEmail());
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El email ya está registrado: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);
        log.info("Usuario creado: id={}, username={}", saved.getId(), saved.getUsername());

        var notification = new SendNotificationRequest(
                saved.getEmail(),
                "Bienvenido " + saved.getUsername() + "! Tu cuenta ha sido creada en Arena Gamer.",
                "EMAIL"
        );
        try {
            restClient.post()
                    .uri(notificationServiceUrl + "/api/v1/notifications/send")
                    .body(notification)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Notificación de bienvenida enviada a {}", saved.getEmail());
        } catch (Exception e) {
            log.error("Error al notificar creación de usuario: {}", e.getMessage());
        }

        return saved;
    }

    public User update(Long id, User datos) {
        User existente = findById(id);
        existente.setUsername(datos.getUsername());
        existente.setEmail(datos.getEmail());
        existente.setRole(datos.getRole());
        User saved = userRepository.save(existente);
        log.info("Usuario actualizado: id={}, username={}", id, saved.getUsername());
        return saved;
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            log.warn("Intento de eliminar usuario inexistente: id={}", id);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id);
        }
        userRepository.deleteById(id);
        log.info("Usuario eliminado: id={}", id);
    }

    public List<User> findByRole(User.Role role) {
        List<User> users = userRepository.findByRole(role);
        log.info("Usuarios con role {}: {}", role, users.size());
        return users;
    }

    public List<User> findActivos() {
        List<User> users = userRepository.findByActiveTrue();
        log.info("Usuarios activos: {}", users.size());
        return users;
    }
}
