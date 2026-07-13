package com.example.userservice.security;

import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        // given
        User user = new User("Fabry27", "fabry27@gmail.com", User.Role.PLAYER);
        user.setPassword("encodedPassword");
        user.setActive(true);

        when(userRepository.findByEmail("fabry27@gmail.com")).thenReturn(Optional.of(user));

        // when
        UserDetails result = customUserDetailsService.loadUserByUsername("fabry27@gmail.com");

        // then
        assertThat(result.getUsername()).isEqualTo("fabry27@gmail.com");
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
        assertThat(result.isEnabled()).isTrue();
        assertThat(result.getAuthorities()).anyMatch(a -> a.getAuthority().equals("ROLE_PLAYER"));
    }

    @Test
    void loadUserByUsername_shouldThrow_whenUserNotFound() {
        // given
        when(userRepository.findByEmail("noexiste@gmail.com")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("noexiste@gmail.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("noexiste@gmail.com");
    }
}