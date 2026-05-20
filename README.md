# Arena Gamer Platform 🎮

## Descripción
Arena Gamer Platform es una solución de gestión para cibercafés y centros de gaming, construida con arquitectura de microservicios usando Spring Boot. Permite gestionar usuarios, estaciones de juego, sesiones, reservas, inventario, billeteras virtuales, torneos, hardware y fidelización de clientes.

## Equipo de Desarrollo - Equipo N°12
- Tomás Recabarren
- Ali Simanca
- Fabrizio Quintini

## Tecnologías Utilizadas
- Java 17/21
- Spring Boot 3.2.0
- Spring Data JPA + Hibernate
- Spring Security (HTTP Basic Auth)
- OpenFeign + RestClient (comunicación inter-servicio)
- Resilience4j (Circuit Breaker / Fallback)
- H2 Database (desarrollo)
- MySQL (producción)
- Lombok
- Maven

## Microservicios Implementados (10)

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| user-service | 8081 | Gestión de usuarios con autenticación |
| station-service | 8083 | Gestión de estaciones de juego |
| session-service | 8082 | Control de sesiones activas |
| arena-inventory | 9001 | Inventario de productos y equipos |
| arena-reservas | 9000 | Reservas de estaciones |
| arena-wallet | 8085 | Billeteras virtuales de usuarios |
| hardware-monitor | 8090 | Monitoreo de temperatura de equipos |
| loyalty-service | 8087 | Sistema de puntos y fidelización |
| notification-service | 8089 | Envío de notificaciones |
| tournament-service | 8088 | Gestión de torneos |

## Comunicaciones Inter-Servicio

| Origen | Destino | Tipo | Evento |
|--------|---------|------|--------|
| session-service | user-service | RestClient | Verifica usuario al iniciar sesión |
| session-service | station-service | RestClient | Verifica estación al iniciar sesión |
| session-service | arena-wallet | RestClient | Descuenta saldo al finalizar sesión |
| session-service | loyalty-service | RestClient | Acredita puntos al finalizar sesión |
| arena-reservas | arena-inventory | Feign | Descuenta stock al confirmar reserva |
| arena-wallet | user-service | Feign | Verifica usuario al crear billetera |
| hardware-monitor | notification-service | RestClient | Alerta cuando CPU>85° o GPU>90° |
| tournament-service | notification-service | Feign + Fallback | Notifica creación de torneo |
| tournament-service | user-service | Feign | Verifica usuario al crear torneo |
| user-service | notification-service | RestClient | Notifica bienvenida al crear usuario |

## Pasos para Ejecutar

### Requisitos Previos
- Java 21 instalado
- IntelliJ IDEA
- Maven

### Instrucciones

1. Clonar el repositorio:
```bash
git clone https://github.com/TomasELegante/Arena-Gamer.git
```

2. Abrir el proyecto en IntelliJ IDEA como proyecto Maven multi-módulo.

3. Ejecutar cada microservicio en el siguiente orden:
   - user-service (8081)
   - station-service (8083)
   - session-service (8082)
   - arena-inventory (9001)
   - arena-reservas (9000)
   - arena-wallet (8085)
   - notification-service (8089)
   - loyalty-service (8087)
   - hardware-monitor (8090)
   - tournament-service (8088)

4. Verificar que todos los servicios estén activos usando las URLs:
GET http://localhost:8081/api/users
GET http://localhost:8083/api/stations
GET http://localhost:8082/api/sessions
GET http://localhost:9001/api/v1/productos
GET http://localhost:9000/api/v1/reservas
GET http://localhost:8085/api/v1/billeteras
GET http://localhost:8089/api/v1/notifications/logs
GET http://localhost:8087/api/v1/loyalty/1
GET http://localhost:8090/api/v1/hardware/status
GET http://localhost:8088/api/v1/tournaments

## Credenciales de Acceso

### user-service (HTTP Basic Auth)
| Usuario | Email | Contraseña | Rol |
|---------|-------|------------|-----|
| Fabry27 | Fabry27@gmail.com | fabry123 | PLAYER |
| Tomas69 | Tomas69@arenagamer.cl | tomas123 | PLAYER |
| MohammedAli | mohammedAli@arenagamer.cl | staff123 | STAFF |

### station-service (HTTP Basic Auth)
| Usuario | Contraseña | Rol |
|---------|------------|-----|
| admin_leo | staff123 | STAFF |
| shadow99 | player123 | PLAYER |
