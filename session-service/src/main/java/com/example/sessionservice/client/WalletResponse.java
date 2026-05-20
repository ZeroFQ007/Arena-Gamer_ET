package com.example.sessionservice.client;

public record WalletResponse(
        Long id,
        Long idUsuario,
        Double saldo,
        Integer puntosFidelizacion
) {}