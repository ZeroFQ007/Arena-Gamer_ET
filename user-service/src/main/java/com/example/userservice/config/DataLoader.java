package com.example.userservice.config;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User fabry = new User("Fabry27", "Fabry27@gmail.com", User.Role.PLAYER);
        fabry.setPassword(passwordEncoder.encode("fabry123"));
        userRepository.save(fabry);

        User tomas = new User("Tomas69", "Tomas69@arenagamer.cl", User.Role.PLAYER);
        tomas.setPassword(passwordEncoder.encode("tomas123"));
        userRepository.save(tomas);

        User mohammed = new User("MohammedAli", "mohammedAli@arenagamer.cl", User.Role.STAFF);
        mohammed.setPassword(passwordEncoder.encode("staff123"));
        userRepository.save(mohammed);
    }
}