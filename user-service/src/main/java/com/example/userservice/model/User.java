package com.example.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import javax.management.relation.Role;

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
    public Long getId() {{return id;}}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public Role getRole() {return role;}
    public void setRole(Role role) {this.role = role;}
    public boolean isActive() {return active;}
    public void setActive(boolean active) {this.active = active;}

}
