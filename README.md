# Arena Gamer Platform 🎮
Sistema de gestión backend para cibercafés y centros de gaming, construido con una arquitectura de microservicios en Spring Boot. Automatiza sesiones de juego, reservas de estaciones, cobros mediante billeteras virtuales, fidelización de clientes, monitoreo de hardware y gestión de torneos.

## Equipo — N°12
- **Tomás Recabarren**
- **Ali Simanca**
- **Fabrizio Quintini**

DSY1103 Desarrollo FullStack 1 — DUOC UC

---

## Arquitectura

El sistema está compuesto por **10 microservicios independientes**, un **API Gateway** y un **Eureka Server**:

```
                          ┌─────────────────┐
                          │   API Gateway   │
                          │   (puerto 8080) │
                          └────────┬────────┘
                                   │
        ┌──────────────┬──────────┼──────────┬──────────────┐
        │              │          │          │              │
   user-service   station-service │   arena-wallet   loyalty-service
     (8081)           (8083)      │      (8085)          (8087)
        │                         │
        └──────────┬──────────────┘
                    │
            session-service
                (8082)
```

| # | Servicio | Puerto | Responsable | Descripción |
|---|---|---|---|---|
| 1 | **user-service** | 8081 | Fabrizio Quintini | Gestión de usuarios y autenticación |
| 2 | **station-service** | 8083 | Fabrizio Quintini | Gestión de estaciones PC/consola |
| 3 | **session-service** | 8082 | Fabrizio Quintini | Control de sesiones activas de juego |
| 4 | **arena-inventory** | 9001 | Tomás Recabarren | Inventario de productos alquilables |
| 5 | **arena-reservas** | 9000 | Tomás Recabarren | Reservas de estaciones con historial |
| 6 | **arena-wallet** | 8085 | Tomás Recabarren | Billeteras virtuales y transacciones |
| 7 | **hardware-monitor** | 8090 | Ali Simanca | Monitoreo de temperatura CPU/GPU |
| 8 | **loyalty-service** | 8087 | Ali Simanca | Puntos de fidelización |
| 9 | **notification-service** | 8089 | Ali Simanca | Registro y envío de notificaciones |
| 10 | **tournament-service** | 8088 | Ali Simanca | Gestión de torneos competitivos |
| — | **api-gateway** | 8080 | Fabrizio Quintini | Enrutamiento centralizado (Spring Cloud Gateway) |
| — | **eureka-server** | 8761 | Ali Simanca | Service Discovery (Netflix Eureka) |

## Tecnologías

- **Java 21** · **Spring Boot 3.2.0**
- **Spring Data JPA + Hibernate** — persistencia con relaciones `@OneToMany` / `@ManyToOne`
- **Spring Security** — autenticación HTTP Basic
- **Spring Cloud Gateway** — API Gateway con filtros globales y por ruta
- **Netflix Eureka** — Service Discovery
- **OpenFeign** + **RestClient** — comunicación inter-servicio con timeouts de 3 segundos
- **Resilience4j** — Circuit Breaker y Fallback
- **Spring HATEOAS** — enlaces de navegación en las respuestas
- **springdoc-openapi** — documentación Swagger/OpenAPI
- **H2** (desarrollo) / **MySQL 8.4** (Docker / producción)
- **JUnit 5 + Mockito** — pruebas unitarias
- **Docker + Docker Compose** — contenerización
- **Lombok**, **Maven** (monorepo multi-módulo)

---

## API Gateway

Todo el tráfico puede pasar a través del Gateway en el puerto **8080**, que enruta automáticamente según el path hacia el microservicio correspondiente:

```
http://localhost:8080/api/users          → user-service (8081)
http://localhost:8080/api/stations       → station-service (8083)
http://localhost:8080/api/sessions       → session-service (8082)
http://localhost:8080/api/v1/productos   → arena-inventory (9001)
http://localhost:8080/api/v1/reservas    → arena-reservas (9000)
http://localhost:8080/api/v1/billeteras  → arena-wallet (8085)
http://localhost:8080/api/v1/loyalty     → loyalty-service (8087)
http://localhost:8080/api/v1/hardware    → hardware-monitor (8090)
http://localhost:8080/api/v1/notifications → notification-service (8089)
http://localhost:8080/api/v1/tournaments → tournament-service (8088)
```

### Filtros del Gateway
- **Global:** `X-Gateway-Source: arena-gamer-gateway` en todos los requests
- **Global:** `X-Response-Gateway: arena-gamer-platform` en todas las respuestas
- **Por ruta:** `X-Service-Name: <nombre-servicio>` identifica el microservicio destino
- **GlobalErrorFilter:** registra rutas, IPs, métodos HTTP y tiempos de respuesta

## Documentación Swagger / OpenAPI

| Servicio | Swagger UI |
|---|---|
| user-service | http://localhost:8081/swagger-ui/index.html |
| station-service | http://localhost:8083/swagger-ui/index.html |
| session-service | http://localhost:8082/swagger-ui/index.html |
| arena-inventory | http://localhost:9001/swagger-ui/index.html |
| arena-reservas | http://localhost:9000/swagger-ui/index.html |
| arena-wallet | http://localhost:8085/swagger-ui/index.html |
| loyalty-service | http://localhost:8087/swagger-ui/index.html |
| hardware-monitor | http://localhost:8090/swagger-ui/index.html |
| notification-service | http://localhost:8089/swagger-ui/index.html |
| tournament-service | http://localhost:8088/swagger-ui/index.html |

## HATEOAS

Las respuestas `GET` de los 10 microservicios incluyen enlaces de navegación `_links`:

```json
{
  "id": 1,
  "username": "Fabry27",
  "_links": {
    "self": { "href": "http://localhost:8081/api/users/1" },
    "all": { "href": "http://localhost:8081/api/users" },
    "update": { "href": "http://localhost:8081/api/users/1" }
  }
}
```

Los enlaces son **condicionales** según el estado del recurso — por ejemplo, una estación en `MAINTENANCE` no expone el link `update`, y una sesión `FINISHED` no expone `finish`/`cancel`.

---

## Relaciones JPA

El proyecto implementa relaciones entre entidades dentro del mismo microservicio:

- **`Station` → `StationMaintenanceLog`**: relación `@OneToMany` — una estación tiene múltiples registros de mantenimiento
- **`StationMaintenanceLog` → `Station`**: relación `@ManyToOne` — cada log pertenece a una estación

```
GET http://localhost:8083/api/stations/{id}/maintenance-logs
```

---

## Comunicación entre microservicios

Todos los clientes REST del `session-service` tienen **timeout de 3 segundos** configurado:

| Origen | Destino | Tipo | Evento |
|---|---|---|---|
| session-service | user-service | RestClient + timeout 3s | Verifica usuario al iniciar sesión |
| session-service | station-service | RestClient + timeout 3s | Verifica estación disponible |
| session-service | arena-wallet | RestClient + timeout 3s | Descuenta saldo al finalizar sesión |
| session-service | loyalty-service | RestClient + timeout 3s | Acredita puntos al finalizar sesión |
| arena-reservas | arena-inventory | Feign | Descuenta stock al confirmar reserva |
| arena-wallet | user-service | Feign | Valida usuario al crear billetera |
| hardware-monitor | notification-service | RestClient | Alerta si CPU>85° o GPU>90° |
| tournament-service | notification-service | Feign + Fallback | Notifica creación de torneo |
| tournament-service | user-service | Feign | Verifica usuario al crear torneo |
| user-service | notification-service | RestClient | Notificación de bienvenida |

Todas las comunicaciones inter-contenedor en Docker usan el **nombre del servicio** (ej. `http://user-service:8081`) en vez de `localhost`, configurado vía variables de entorno (`@Value` con propiedades en `application.yml` y overrides en `compose.yml`).

---

## Eureka Server

El sistema incluye un servidor de descubrimiento de servicios (Netflix Eureka):

```
http://localhost:8761
```

---

## Docker

### Requisitos
- Docker Desktop
- Docker Compose v2

### Levantar todo el sistema

```bash
docker compose up -d --build
docker compose ps
```

Esto levanta MySQL y los 10 microservicios, conectados entre sí por red Docker interna.

### Ver logs

```bash
docker compose logs -f <nombre-servicio>
```

### Apagar

```bash
docker compose down       # detiene, conserva datos
docker compose down -v    # detiene y borra el volumen de MySQL
```

### Sistemas operativos
- **Windows:** Docker Desktop + WSL2
- **Linux:** Docker Engine
- **macOS:** Docker Desktop

### Dockerfile (patrón usado en los 10 microservicios)

```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar app.jar
RUN groupadd -r authgroup && useradd -r -g authgroup authuser
USER authuser
EXPOSE <puerto>
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Cada contenedor corre como usuario **no-root** (`authuser`) por seguridad.

---

## Ejecución local (sin Docker)

### Requisitos
- Java 21 (recomendado: distribución Microsoft `ms-21.0.10`)
- IntelliJ IDEA
- Maven (incluido vía `mvnw`)

### Pasos

1. Clonar el repositorio:
```bash
git clone https://github.com/TomasELegante/Arena-Gamer.git
```

2. Abrir como proyecto Maven multi-módulo en IntelliJ.

3. Ejecutar los servicios en este orden:

| Orden | Servicio |
|---|---|
| 1° | eureka-server |
| 2° | user-service |
| 3° | station-service |
| 4° | notification-service |
| 5° | arena-inventory |
| 6° | arena-wallet |
| 7° | loyalty-service |
| 8° | session-service |
| 9° | arena-reservas |
| 10° | hardware-monitor |
| 11° | tournament-service |
| 12° | api-gateway |

4. Verificar con GET (perfil H2 por defecto, sin Docker):

```
http://localhost:8761               ← Eureka Dashboard
http://localhost:8081/api/users
http://localhost:8083/api/stations
http://localhost:8082/api/sessions
http://localhost:9001/api/v1/productos
http://localhost:9000/api/v1/reservas
http://localhost:8085/api/v1/billeteras
http://localhost:8087/api/v1/loyalty/1
http://localhost:8090/api/v1/hardware/status
http://localhost:8089/api/v1/notifications/logs
http://localhost:8088/api/v1/tournaments
http://localhost:8080/api/users  ← a través del Gateway
```

---

## Credenciales

### user-service (autenticación por **email**)
| Email | Contraseña | Rol |
|---|---|---|
| Fabry27@gmail.com | fabry123 | PLAYER |
| Tomas69@arenagamer.cl | tomas123 | PLAYER |
| mohammedAli@arenagamer.cl | staff123 | STAFF |

### station-service (autenticación por **username**, InMemory)
| Username | Contraseña | Rol |
|---|---|---|
| admin_leo | staff123 | STAFF |
| shadow99 | player123 | PLAYER |

---

## Pruebas Unitarias

7 de 10 microservicios cuentan con pruebas unitarias usando **JUnit 5** y **Mockito**, siguiendo el patrón **Given-When-Then**:

```bash
mvnw.cmd test                          # todas las pruebas del módulo
mvnw.cmd test -Dtest=NombreClaseTest    # una clase específica
```

### Plan de pruebas

**Alcance:** lógica de negocio de los servicios (`*Service`), validaciones, y manejo de errores. No incluye base de datos real ni llamadas a servicios externos reales (se usan mocks).

| Servicio | Test | Casos cubiertos |
|---|---|---|
| user-service | `UserServiceTest`, `UserControllerTest` | crear/buscar/actualizar/eliminar, duplicados |
| station-service | `StationServiceTest` | CRUD, duplicados de nombre, filtros por tipo/disponibilidad |
| session-service | `SessionServiceTest` | iniciar/finalizar/cancelar sesión, validaciones de estado |
| arena-inventory | `ProductoServiceTest`, `ProductoControllerTest` | CRUD, actualización de stock |
| arena-wallet | `BilleteraServiceTest`, `BilleteraControllerTest` | crear billetera, recarga, descuento, saldo insuficiente |
| arena-reservas | `ReservaServiceTest` | conflictos de horario, fechas pasadas, cambio de estado |
| loyalty-service | `LoyaltyServiceTest` | acreditar/canjear puntos, niveles de fidelización |

---

## Bug conocido (mejora pendiente)

La conexión `arena-reservas → arena-inventory` usa `estacionId` como si fuera `productoId` del inventario, ya que ambos son conceptos distintos (estación física vs. producto alquilable). Una mejora futura agregaría un campo `productoId` explícito a la reserva para relacionar correctamente ambos recursos.

---

## Variables de entorno

Ver `.env.example` en la raíz del proyecto para la configuración completa.

## Colección de endpoints

Ver `arena-gamer.http` en la raíz del proyecto para probar todos los endpoints directamente desde IntelliJ.

## Documentación adicional

Ver carpeta `/docs`:
- `documentacion-tecnica.md`
- `documentacion-funcional.md`
- `matriz-requerimientos.md`
- `plan-cierre-feedback.md`
- `defensa-individual/quintini-fabrizio.md`
- `defensa-individual/ali-simanca.md`

---

## Estructura del repositorio

```
Arena-Gamer/
├── api-gateway/
├── eureka-server/
├── user-service/
├── station-service/
├── session-service/
├── arena-inventory/
├── arena-reservas/
├── arena-wallet/
├── hardware-monitor/
├── loyalty-service/
├── notification-service/
├── tournament-service/
├── docs/
│   ├── documentacion-tecnica.md
│   ├── documentacion-funcional.md
│   ├── matriz-requerimientos.md
│   ├── plan-cierre-feedback.md
│   └── defensa-individual/
│       ├── quintini-fabrizio.md
│       └── ali-simanca.md
├── arena-gamer.http
├── .env.example
├── compose.yml
├── docker-compose.yml
├── render.yaml
└── pom.xml
```
