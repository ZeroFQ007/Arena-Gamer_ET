# Defensa Individual — Fabrizio Quintini

## Información Personal
- **Nombre:** Fabrizio Quintini
- **GitHub:** ZeroFQ007
- **Curso:** DSY1103 Desarrollo FullStack 1
- **Equipo:** N°12

## Microservicios bajo mi responsabilidad

| Servicio | Puerto | Descripción |
|---|---|---|
| user-service | 8081 | Gestión de usuarios y autenticación |
| station-service | 8083 | Gestión de estaciones PC/consola |
| session-service | 8082 | Control de sesiones de juego |
| api-gateway | 8080 | Enrutamiento centralizado |

## Commits destacados

- `feat: agregar API Gateway con Spring Cloud Gateway enrutando a los 10 microservicios`
- `feat: agregar filtros al api-gateway (AddRequestHeader y AddResponseHeader por ruta)`
- `feat: agregar Dockerfile y containerizar api-gateway con rutas Docker en compose.yml`
- `fix: evitar duplicados en DataLoader verificando si ya existen usuarios`
- `fix: evitar duplicados en DataLoader de station-service`
- `fix: usar nombre de servicio Docker en vez de localhost para UserClient y StationClient`
- `fix: usar nombre de servicio Docker en vez de localhost para WalletClient y LoyaltyClient`
- `test: agregar pruebas unitarias para StationService con JUnit5 y Mockito`
- `test: agregar tests para dto, exception y security - cobertura 82%`
- `docs: actualizar README con Gateway, Swagger, HATEOAS y testing`

## Clases que puedo explicar

### user-service
- `UserController` — endpoints REST con ResponseEntity, HATEOAS y Swagger
- `UserService` — lógica de negocio: crear usuario, validar duplicados, notificar
- `UserRepository` — JPA con métodos findByEmail, findByRole, findByActiveTrue
- `User` — entidad JPA con enum Role (PLAYER/STAFF)
- `SecurityConfig` — Spring Security con HTTP Basic y permisos por rol
- `CustomUserDetailsService` — carga usuario desde BD por email
- `GlobalExceptionHandler` — manejo de excepciones con @ControllerAdvice
- `DataLoader` — carga datos iniciales con protección anti-duplicados
- `UserLinkAssembler` — construye links HATEOAS condicionales

### station-service
- `StationController` — CRUD completo con roles (GET público, POST/PUT/DELETE solo STAFF)
- `StationService` — lógica: crear, actualizar, eliminar con validaciones
- `StationRepository` — JPA con findByType, findByStatus, findByAvailableTrue
- `Station` — entidad con enums StationType (PC/CONSOLE) y StationStatus
- `StationLinkAssembler` — links condicionales (update solo si available=true)

### session-service
- `SessionController` — iniciar, finalizar, cancelar sesiones
- `SessionService` — lógica de negocio principal del sistema
- `UserClient` — RestClient que llama a user-service para verificar usuario
- `StationClient` — RestClient que llama a station-service
- `WalletClient` — RestClient que descuenta saldo al finalizar sesión
- `LoyaltyClient` — RestClient que acredita puntos al finalizar sesión

### api-gateway
- `ApiGatewayApplication` — clase principal del Gateway
- `application.yml` — rutas, filtros globales y por ruta, variables de entorno

## Lógica de negocio que puedo explicar

### Flujo de sesión completo
1. POST /api/sessions → session-service valida usuario (user-service) y estación (station-service)
2. Sesión queda ACTIVE
3. PUT /api/sessions/{id}/finish → calcula duración en minutos
4. Costo = duración × $10 CLP → descuenta saldo (arena-wallet)
5. Puntos = duración / 10 (mínimo 1) → acredita puntos (loyalty-service)
6. Sesión queda FINISHED

### Comunicación Docker
- Dentro de Docker los servicios se llaman por nombre: `http://user-service:8081`
- Las URLs están configuradas con `@Value` y variables de entorno en compose.yml
- El fallback en caso de fallo devuelve "Desconocido" sin romper la sesión

## Pruebas unitarias

| Clase | Tests | Cobertura |
|---|---|---|
| UserServiceTest | 11 tests | 78% service |
| UserControllerTest | 8 tests | 100% controller |
| UserDtoTest | 5 tests | 86% dto |
| GlobalExceptionHandlerTest | 5 tests | 64% exception |
| CustomUserDetailsServiceTest | 2 tests | 86% security |
| StationServiceTest | 10 tests | service completo |
| **Total user-service** | **32 tests** | **82%** |