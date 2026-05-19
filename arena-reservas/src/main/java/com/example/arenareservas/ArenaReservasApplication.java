package com.example.arenareservas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ArenaReservasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArenaReservasApplication.class, args);
    }
}