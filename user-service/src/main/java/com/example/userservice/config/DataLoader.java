package com.example.userservice.config;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        userRepository.save(new User("Fabri27",   "Fabri27@gmail.com",  User.Role.PLAYER));
        userRepository.save(new User("Tomas69", "Tomas69@arenagamer.cl", User.Role.PLAYER));
        userRepository.save(new User("MohammedAli",  "mohammedAli@arenagamer.cl",   User.Role.STAFF));
    }
}