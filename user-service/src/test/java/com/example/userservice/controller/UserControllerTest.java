package com.example.userservice.controller;

import com.example.userservice.model.User;
import com.example.userservice.service.UserLinkAssembler;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserLinkAssembler userLinkAssembler;

    @Test
    void getAll_Returns200() throws Exception {
        User user = new User("gamer1", "gamer1@test.com", User.Role.PLAYER);
        user.setId(1L);
        when(userService.findAll()).thenReturn(List.of(user));
        when(userLinkAssembler.toModel(user)).thenReturn(EntityModel.of(user));

        mockMvc.perform(get("/api/users").with(user("admin").roles("PLAYER", "STAFF")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userList[0].username").value("gamer1"));
    }

    @Test
    void getById_WhenExists_Returns200() throws Exception {
        User user = new User("gamer1", "gamer1@test.com", User.Role.PLAYER);
        user.setId(1L);
        when(userService.findById(1L)).thenReturn(user);
        when(userLinkAssembler.toModel(user)).thenReturn(EntityModel.of(user));

        mockMvc.perform(get("/api/users/1").with(user("admin").roles("PLAYER", "STAFF")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("gamer1"));
    }

    @Test
    void getById_WhenNotExists_Returns404() throws Exception {
        when(userService.findById(99L))
                .thenThrow(new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        mockMvc.perform(get("/api/users/99").with(user("admin").roles("PLAYER", "STAFF")))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_WithValidData_Returns201() throws Exception {
        User user = new User("gamer1", "gamer1@test.com", User.Role.PLAYER);
        user.setId(1L);
        user.setPassword("encoded");
        when(userService.create(any(User.class))).thenReturn(user);

        String json = """
                {"username":"gamer1","email":"gamer1@test.com","role":"PLAYER","password":"pass123"}""";

        mockMvc.perform(post("/api/users")
                        .with(user("admin").roles("PLAYER", "STAFF"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void update_WhenExists_Returns200() throws Exception {
        User user = new User("updated", "updated@test.com", User.Role.STAFF);
        user.setId(1L);
        when(userService.update(eq(1L), any(User.class))).thenReturn(user);

        String json = """
                {"username":"updated","email":"updated@test.com","role":"STAFF","password":"newpass"}""";

        mockMvc.perform(put("/api/users/1")
                        .with(user("admin").roles("PLAYER", "STAFF"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated"));
    }

    @Test
    void delete_WhenExists_Returns204() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/1")
                        .with(user("admin").roles("PLAYER", "STAFF"))
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void getByRole_Returns200() throws Exception {
        when(userService.findByRole(User.Role.PLAYER)).thenReturn(List.of());

        mockMvc.perform(get("/api/users/role/PLAYER").with(user("admin").roles("PLAYER", "STAFF")))
                .andExpect(status().isOk());
    }

    @Test
    void getActivos_Returns200() throws Exception {
        when(userService.findActivos()).thenReturn(List.of());

        mockMvc.perform(get("/api/users/active").with(user("admin").roles("PLAYER", "STAFF")))
                .andExpect(status().isOk());
    }
}
