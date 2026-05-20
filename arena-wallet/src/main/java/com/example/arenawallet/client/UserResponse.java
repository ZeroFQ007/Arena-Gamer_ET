package com.example.arenawallet.client;

public record UserResponse(
        Long id,
        String username,
        String email,
        String role,
        boolean active
) {}