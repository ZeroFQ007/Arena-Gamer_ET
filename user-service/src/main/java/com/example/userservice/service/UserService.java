package com.example.userservice.service;

import com.example.userservice.dto.SendNotificationRequest;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id));
    }

    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El username ya está en uso: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "El email ya está registrado: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User saved = userRepository.save(user);

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
        } catch (Exception e) {
            System.err.println("Error al notificar creación de usuario: " + e.getMessage());
        }

        return saved;
    }

    public User update(Long id, User datos) {
        User existente = findById(id);
        existente.setUsername(datos.getUsername());
        existente.setEmail(datos.getEmail());
        existente.setRole(datos.getRole());
        return userRepository.save(existente);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuario no encontrado con id: " + id);
        }
        userRepository.deleteById(id);
    }

    public List<User> findByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    public List<User> findActivos() {
        return userRepository.findByActiveTrue();
    }
}
