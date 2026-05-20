package com.example.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


@Entity
@Table(name ="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El username no puede estar vacio")
    @Column(unique = true, nullable = false)
    private String username;

    @Email(message ="Debe tener un email valido")
    @NotBlank(message = "El email no puede estar vacio")
    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @JsonIgnore
    @Column(length = 255)
    private String password;

    @Column (nullable = false)
    private boolean active = true;

    public enum Role {
        PLAYER, STAFF
    }
    public User (){}

    public User(String username, String email, Role role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }
    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public Role getRole() {return role;}
    public void setRole(Role role) {this.role = role;}
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    public boolean isActive() {return active;}
    public void setActive(boolean active) {this.active = active;}

}
