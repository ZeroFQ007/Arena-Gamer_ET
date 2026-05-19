package com.example.arenawallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ArenaWalletApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArenaWalletApplication.class, args);
    }

}
