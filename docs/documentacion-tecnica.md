# Documentación Técnica — Arena Gamer Platform

## Arquitectura

Sistema de microservicios Spring Boot para gestión de cibercafés. Cada microservicio es independiente con su propia base de datos (H2 en desarrollo, MySQL en producción).

## Stack Tecnológico

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 3.2.0 | Framework base |
| Spring Cloud Gateway | 2023.0.0 | API Gateway |
| Spring Data JPA | 3.2.0 | Persistencia |
| Spring Security | 6.2.0 | Autenticación |
| Spring HATEOAS | 3.2.0 | Enlaces hipermedia |
| springdoc-openapi | 2.3.0 | Documentación Swagger |
| H2 | 2.2.x | Base de datos desarrollo |
| MySQL | 8.4 | Base de datos producción |
| Docker | 29.x | Contenerización |
| Docker Compose | v5.x | Orquestación local |
| JUnit 5 | 5.10.x | Pruebas unitarias |
| Mockito | 5.x | Mocks para pruebas |
| Lombok | 1.18.34 | Reducción de boilerplate |
| JaCoCo | 0.8.11 | Cobertura de pruebas |

## Microservicios

| # | Servicio | Puerto | Responsable | Descripción |
|---|---|---|---|---|
| 1 | user-service | 8081 | Fabrizio Quintini | Gestión de usuarios y autenticación |
| 2 | station-service | 8083 | Fabrizio Quintini | Gestión de estaciones PC/consola |
| 3 | session-service | 8082 | Fabrizio Quintini | Control de sesiones de juego |
| 4 | arena-inventory | 9001 | Tomás Recabarren | Inventario de productos alquilables |
| 5 | arena-reservas | 9000 | Tomás Recabarren | Reservas de estaciones |
| 6 | arena-wallet | 8085 | Tomás Recabarren | Billeteras virtuales |
| 7 | hardware-monitor | 8090 | Ali Simanca | Monitoreo de temperatura CPU/GPU |
| 8 | loyalty-service | 8087 | Ali Simanca | Puntos de fidelización |
| 9 | notification-service | 8089 | Ali Simanca | Notificaciones del sistema |
| 10 | tournament-service | 8088 | Ali Simanca | Torneos competitivos |
| — | api-gateway | 8080 | Fabrizio Quintini | Enrutamiento centralizado |

## Patrón de Arquitectura

Cada microservicio sigue el patrón **CSR (Controller → Service → Repository)**:

Controller → recibe la petición HTTP y delega al Service
Service    → contiene la lógica de negocio
Repository → accede a la base de datos via JPA
Model      → entidad JPA que mapea la tabla
DTO        → objeto de transferencia de datos


## Comunicación entre Microservicios

| Origen | Destino | Tecnología | Evento |
|---|---|---|---|
| session-service | user-service | RestClient | Verifica usuario al iniciar sesión |
| session-service | station-service | RestClient | Verifica estación disponible |
| session-service | arena-wallet | RestClient | Descuenta saldo al finalizar |
| session-service | loyalty-service | RestClient | Acredita puntos al finalizar |
| arena-reservas | arena-inventory | OpenFeign | Descuenta stock al confirmar reserva |
| arena-wallet | user-service | OpenFeign | Valida usuario al crear billetera |
| hardware-monitor | notification-service | RestClient | Alerta si CPU>85° o GPU>90° |
| tournament-service | notification-service | OpenFeign+Fallback | Notifica creación de torneo |
| tournament-service | user-service | OpenFeign | Verifica usuario al crear torneo |
| user-service | notification-service | RestClient | Bienvenida al registrar usuario |

## API Gateway

Spring Cloud Gateway en puerto 8080 enruta hacia los 10 microservicios.

**Filtros globales:**
- `AddRequestHeader=X-Gateway-Source, arena-gamer-gateway`
- `AddResponseHeader=X-Response-Gateway, arena-gamer-platform`

**Filtro por ruta:**
- `AddRequestHeader=X-Service-Name, <nombre-servicio>`

## Base de Datos

### Perfiles
- **h2** (por defecto): Base de datos en memoria, ideal para desarrollo y pruebas
- **mysql**: MySQL 8.4, usado en Docker y producción

### Configuración MySQL (Docker)
Host: mysql (nombre de servicio Docker)
Puerto: 3306
Base de datos: arena_gamer_db
Usuario: arena
Contraseña: arena123

## Docker

### Levantar todo el sistema
```bash
docker compose up -d --build
docker compose ps
```

### Dockerfile (patrón usado en todos los microservicios)
```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar app.jar
RUN groupadd -r authgroup && useradd -r -g authgroup authuser
USER authuser
EXPOSE <puerto>
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Todos los contenedores corren como usuario **no-root** (`authuser`) por seguridad.

## Pruebas Unitarias

- Framework: JUnit 5 + Mockito
- Patrón: Given / When / Then
- Cobertura user-service: **82%**
- Servicios con pruebas: user-service, station-service, session-service, arena-inventory, arena-wallet, arena-reservas, loyalty-service

### Ejecutar pruebas
```bash
cd <nombre-servicio>
.\mvnw.cmd test
.\mvnw.cmd jacoco:report
```

## Seguridad

- **user-service**: Spring Security con autenticación por email (BCrypt)
- **station-service**: Spring Security con usuarios InMemory
- **Swagger UI**: Acceso público configurado en SecurityConfig
- Todos los contenedores Docker corren como usuario no-root