package com.example.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Arena Gamer - User Service API")
                        .description("Microservicio de gestión de usuarios para Arena Gamer Platform")
                        .version("1.0.0")
                        .license(new License().name("Uso educativo DSY1103"))
                        .contact(new Contact()
                                .name("Equipo N°12")
                                .email("arenagamer@duoc.cl")));
    }
}