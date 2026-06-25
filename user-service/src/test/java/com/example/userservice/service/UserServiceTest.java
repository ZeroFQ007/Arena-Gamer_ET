package com.example.userservice.service;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestClient restClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void findAll_ReturnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));

        List<User> result = userService.findAll();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void findById_WhenExists_ReturnsUser() {
        User user = new User("gamer1", "gamer1@test.com", User.Role.PLAYER);
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("gamer1", result.getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void findById_WhenNotExists_Throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.findById(99L));
    }

    @Test
    void create_WithValidData_SavesAndReturns() {
        User user = new User("gamer1", "gamer1@test.com", User.Role.PLAYER);
        user.setPassword("rawPassword");

        when(userRepository.existsByUsername("gamer1")).thenReturn(false);
        when(userRepository.existsByEmail("gamer1@test.com")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        User saved = new User("gamer1", "gamer1@test.com", User.Role.PLAYER);
        saved.setId(1L);
        saved.setPassword("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.create(user);

        assertEquals(1L, result.getId());
        assertEquals("gamer1", result.getUsername());
        verify(passwordEncoder).encode("rawPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_WhenUsernameExists_Throws() {
        User user = new User("gamer1", "gamer1@test.com", User.Role.PLAYER);
        when(userRepository.existsByUsername("gamer1")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> userService.create(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_WhenEmailExists_Throws() {
        User user = new User("gamer1", "gamer1@test.com", User.Role.PLAYER);
        when(userRepository.existsByUsername("gamer1")).thenReturn(false);
        when(userRepository.existsByEmail("gamer1@test.com")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> userService.create(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_WhenExists_UpdatesAndReturns() {
        User existing = new User("oldName", "old@test.com", User.Role.PLAYER);
        existing.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        User datos = new User("newName", "new@test.com", User.Role.STAFF);
        when(userRepository.save(any(User.class))).thenReturn(existing);

        User result = userService.update(1L, datos);

        assertEquals("newName", result.getUsername());
        assertEquals("new@test.com", result.getEmail());
        assertEquals(User.Role.STAFF, result.getRole());
        verify(userRepository).save(existing);
    }

    @Test
    void delete_WhenExists_Deletes() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_WhenNotExists_Throws() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> userService.delete(99L));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void findByRole_ReturnsFiltered() {
        User user = new User("gamer1", "gamer1@test.com", User.Role.PLAYER);
        when(userRepository.findByRole(User.Role.PLAYER)).thenReturn(List.of(user));

        List<User> result = userService.findByRole(User.Role.PLAYER);

        assertEquals(1, result.size());
        assertEquals(User.Role.PLAYER, result.get(0).getRole());
    }

    @Test
    void findActivos_ReturnsActiveUsers() {
        User user = new User("gamer1", "gamer1@test.com", User.Role.PLAYER);
        user.setActive(true);
        when(userRepository.findByActiveTrue()).thenReturn(List.of(user));

        List<User> result = userService.findActivos();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
    }
}
