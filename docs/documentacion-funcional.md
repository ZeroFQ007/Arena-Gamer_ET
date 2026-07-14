# Documentación Funcional — Arena Gamer Platform

## Descripción del Sistema

Arena Gamer Platform es un sistema backend para la gestión de cibercafés y centros de gaming. Automatiza los procesos de sesiones de juego, reservas, cobros, fidelización de clientes, monitoreo de hardware y torneos.

## Actores del Sistema

| Actor | Descripción |
|---|---|
| **PLAYER** | Usuario registrado que juega en el cibercafé |
| **STAFF** | Administrador con permisos para gestionar el sistema |

## Funcionalidades por Microservicio

### user-service (Puerto 8081)
- Registrar nuevos usuarios (PLAYER o STAFF)
- Autenticar usuarios con email y contraseña
- Listar, buscar, actualizar y eliminar usuarios
- Notificar bienvenida al registrar un usuario nuevo

### station-service (Puerto 8083)
- Gestionar estaciones PC y CONSOLE
- Listar estaciones disponibles, en mantenimiento u offline
- Filtrar por tipo (PC / CONSOLE)
- Solo STAFF puede crear, actualizar o eliminar estaciones

### session-service (Puerto 8082)
- Iniciar sesión de juego: valida usuario y estación
- Finalizar sesión: calcula costo ($10 CLP/min), descuenta saldo y acredita puntos
- Cancelar sesión activa
- Listar sesiones por usuario o estación

### arena-inventory (Puerto 9001)
- CRUD de productos alquilables (CONSOLA, PERIFERICO, JUEGO)
- Actualizar stock al confirmar reserva
- Filtrar por categoría

### arena-reservas (Puerto 9000)
- Crear reservas de estaciones con validación de conflictos de horario
- Confirmar o cancelar reservas con historial de cambios
- Filtrar por estado (NUEVA, CONFIRMADA, CANCELADA)

### arena-wallet (Puerto 8085)
- Crear billetera virtual por usuario
- Recargar saldo manualmente
- Descontar saldo automáticamente al finalizar sesión
- Ver historial de transacciones

### hardware-monitor (Puerto 8090)
- Registrar temperatura de CPU y GPU de cada PC
- Enviar alerta a notification-service si CPU>85° o GPU>90°
- Listar estado actual de todos los equipos

### loyalty-service (Puerto 8087)
- Acreditar puntos al finalizar sesión (1 punto por cada 10 minutos)
- Canjear puntos por premios (costo: 500 puntos)
- Niveles: BRONCE (0-499), PLATA (500-999), ORO (1000+)

### notification-service (Puerto 8089)
- Registrar y enviar notificaciones del sistema
- Ver historial de notificaciones enviadas

### tournament-service (Puerto 8088)
- Crear torneos verificando que el organizador exista
- Notificar creación de torneo con fallback si notification-service falla
- Listar torneos disponibles

### api-gateway (Puerto 8080)
- Punto de entrada único para todos los microservicios
- Enruta según el path de la URL
- Agrega headers de trazabilidad en cada request

## Reglas de Negocio Principales

1. Un usuario no puede tener dos sesiones activas simultáneas
2. Una estación no puede estar en dos sesiones activas simultáneas
3. El costo de una sesión es $10 CLP por minuto
4. Se acredita 1 punto de lealtad por cada 10 minutos de sesión completada
5. Para canjear puntos se necesitan mínimo 500 puntos
6. El nivel de lealtad cambia automáticamente según el saldo de puntos
7. Las reservas no pueden tener conflicto de horario en la misma estación
8. Una estación en MAINTENANCE u OFFLINE no puede recibir sesiones
9. Si notification-service falla, la creación del torneo igual se completa (fallback)
10. El hardware-monitor alerta automáticamente si la temperatura supera los umbrales

## Endpoints Principales

### A través del API Gateway (puerto 8080)

Vale, perfecto te aviso apenas llegue

Perfecto, mientras llegas voy preparando los borradores de los documentos para que cuando llegues solo copies, pegues y hagas commit. Nos vemos a las 19:00. 🎮

Llegue, son las 19:40

Perfecto, arrancamos. Vamos directo a los documentos.

Primero crea la carpeta docs en la raíz del proyecto en IntelliJ:

Clic derecho sobre la carpeta raíz del proyecto
New → Directory
Nombre: docs
Y dentro de docs crea otra carpeta:

defensa-individual
¿Listo?

Claude presta atencion, necesito que me hagas un prompt donde en caso de tu quedarte sin token poder darselo a otra ia y que entienda el contexto de lo que estamos haciendo y de lo que vamos hacer.

Junto a ello ahora aprovechando que volvista a tener token decirte que son las 21:37.
Orden, avanzan lo más que puedas sin mi aprobacion, no te detengas casa paso a preguntarme si estoy listo avanza con todo como cohete!

Hice lo ultimo que pediste que es crear la carpeta docs con defensa-individual

Perfecto. Aquí el prompt de contexto para otra IA por si se acaba el token:

CONTEXTO PARA CONTINUAR:
Soy Fabrizio Quintini, estudiante de Informática DUOC UC, curso DSY1103. Estoy trabajando en Arena Gamer Platform — monorepo Spring Boot con 10 microservicios para gestión de cibercafés. Repositorio: https://github.com/TomasELegante/Arena-Gamer (rama main). Fork mío: https://github.com/ZeroFQ007/Arena-Gamer_ET.

Estado actual: Tenemos implementado Swagger, HATEOAS, Docker Compose con los 10 microservicios + MySQL + API Gateway con filtros, tests unitarios (user-service 82%). La entrega es a las 23:20 de hoy.

Lo que falta hacer AHORA (en orden de prioridad):

Crear archivos en /docs: documentacion-tecnica.md, documentacion-funcional.md, plan-cierre-feedback.md, matriz-requerimientos.md
Crear docs/defensa-individual/quintini-fabrizio.md
Crear .env.example en la raíz
Exportar colección Postman como archivo .http o JSON
Si hay tiempo: Eureka Server
Los 10 microservicios:

user-service (8081), station-service (8083), session-service (8082) — Fabrizio
arena-inventory (9001), arena-reservas (9000), arena-wallet (8085) — Tomás
hardware-monitor (8090), loyalty-service (8087), notification-service (8089), tournament-service (8088) — Ali
api-gateway (8080) — Fabrizio
Puertos, credenciales, reglas: igual que el README en el repositorio.

Ahora arrancamos sin parar. Crea estos archivos uno por uno en IntelliJ (clic derecho sobre docs → New → File):

Archivo 1: docs/documentacion-tecnica.md

markdown
# Documentación Técnica — Arena Gamer Platform

## Arquitectura

Sistema de microservicios Spring Boot para gestión de cibercafés. Cada microservicio es independiente con su propia base de datos (H2 en desarrollo, MySQL en producción).

## Stack Tecnológico

|
Tecnología
|
Versión
|
Uso
|
|
---
|
---
|
---
|
|
Java
|
21
|
Lenguaje principal
|
|
Spring Boot
|
3.2.0
|
Framework base
|
|
Spring Cloud Gateway
|
2023.0.0
|
API Gateway
|
|
Spring Data JPA
|
3.2.0
|
Persistencia
|
|
Spring Security
|
6.2.0
|
Autenticación
|
|
Spring HATEOAS
|
3.2.0
|
Enlaces hipermedia
|
|
springdoc-openapi
|
2.3.0
|
Documentación Swagger
|
|
H2
|
2.2.x
|
Base de datos desarrollo
|
|
MySQL
|
8.4
|
Base de datos producción
|
|
Docker
|
29.x
|
Contenerización
|
|
Docker Compose
|
v5.x
|
Orquestación local
|
|
JUnit 5
|
5.10.x
|
Pruebas unitarias
|
|
Mockito
|
5.x
|
Mocks para pruebas
|
|
Lombok
|
1.18.34
|
Reducción de boilerplate
|
|
JaCoCo
|
0.8.11
|
Cobertura de pruebas
|

## Microservicios

|
#
|
Servicio
|
Puerto
|
Responsable
|
Descripción
|
|
---
|
---
|
---
|
---
|
---
|
|
1
|
user-service
|
8081
|
Fabrizio Quintini
|
Gestión de usuarios y autenticación
|
|
2
|
station-service
|
8083
|
Fabrizio Quintini
|
Gestión de estaciones PC/consola
|
|
3
|
session-service
|
8082
|
Fabrizio Quintini
|
Control de sesiones de juego
|
|
4
|
arena-inventory
|
9001
|
Tomás Recabarren
|
Inventario de productos alquilables
|
|
5
|
arena-reservas
|
9000
|
Tomás Recabarren
|
Reservas de estaciones
|
|
6
|
arena-wallet
|
8085
|
Tomás Recabarren
|
Billeteras virtuales
|
|
7
|
hardware-monitor
|
8090
|
Ali Simanca
|
Monitoreo de temperatura CPU/GPU
|
|
8
|
loyalty-service
|
8087
|
Ali Simanca
|
Puntos de fidelización
|
|
9
|
notification-service
|
8089
|
Ali Simanca
|
Notificaciones del sistema
|
|
10
|
tournament-service
|
8088
|
Ali Simanca
|
Torneos competitivos
|
|
—
|
api-gateway
|
8080
|
Fabrizio Quintini
|
Enrutamiento centralizado
|

## Patrón de Arquitectura

Cada microservicio sigue el patrón **CSR (Controller → Service → Repository)**:
Controller → recibe la petición HTTP y delega al Service
Service → contiene la lógica de negocio
Repository → accede a la base de datos via JPA
Model → entidad JPA que mapea la tabla
DTO → objeto de transferencia de datos


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
  Archivo 2: docs/documentacion-funcional.md

markdown
# Documentación Funcional — Arena Gamer Platform

## Descripción del Sistema

Arena Gamer Platform es un sistema backend para la gestión de cibercafés y centros de gaming. Automatiza los procesos de sesiones de juego, reservas, cobros, fidelización de clientes, monitoreo de hardware y torneos.

## Actores del Sistema

|
Actor
|
Descripción
|
|
---
|
---
|
|
**
PLAYER
**
|
Usuario registrado que juega en el cibercafé
|
|
**
STAFF
**
|
Administrador con permisos para gestionar el sistema
|

## Funcionalidades por Microservicio

### user-service (Puerto 8081)
- Registrar nuevos usuarios (PLAYER o STAFF)
- Autenticar usuarios con email y contraseña
- Listar, buscar, actualizar y eliminar usuarios
- Notificar bienvenida al registrar un usuario nuevo

### station-service (Puerto 8083)
- Gestionar estaciones PC y CONSOLE
- Listar estaciones disponibles, en mantenimiento u offline
- Filtrar por tipo (PC / CONSOLE)
- Solo STAFF puede crear, actualizar o eliminar estaciones

### session-service (Puerto 8082)
- Iniciar sesión de juego: valida usuario y estación
- Finalizar sesión: calcula costo ($10 CLP/min), descuenta saldo y acredita puntos
- Cancelar sesión activa
- Listar sesiones por usuario o estación

### arena-inventory (Puerto 9001)
- CRUD de productos alquilables (CONSOLA, PERIFERICO, JUEGO)
- Actualizar stock al confirmar reserva
- Filtrar por categoría

### arena-reservas (Puerto 9000)
- Crear reservas de estaciones con validación de conflictos de horario
- Confirmar o cancelar reservas con historial de cambios
- Filtrar por estado (NUEVA, CONFIRMADA, CANCELADA)

### arena-wallet (Puerto 8085)
- Crear billetera virtual por usuario
- Recargar saldo manualmente
- Descontar saldo automáticamente al finalizar sesión
- Ver historial de transacciones

### hardware-monitor (Puerto 8090)
- Registrar temperatura de CPU y GPU de cada PC
- Enviar alerta a notification-service si CPU>85° o GPU>90°
- Listar estado actual de todos los equipos

### loyalty-service (Puerto 8087)
- Acreditar puntos al finalizar sesión (1 punto por cada 10 minutos)
- Canjear puntos por premios (costo: 500 puntos)
- Niveles: BRONCE (0-499), PLATA (500-999), ORO (1000+)

### notification-service (Puerto 8089)
- Registrar y enviar notificaciones del sistema
- Ver historial de notificaciones enviadas

### tournament-service (Puerto 8088)
- Crear torneos verificando que el organizador exista
- Notificar creación de torneo con fallback si notification-service falla
- Listar torneos disponibles

### api-gateway (Puerto 8080)
- Punto de entrada único para todos los microservicios
- Enruta según el path de la URL
- Agrega headers de trazabilidad en cada request

## Reglas de Negocio Principales

1. Un usuario no puede tener dos sesiones activas simultáneas
2. Una estación no puede estar en dos sesiones activas simultáneas
3. El costo de una sesión es $10 CLP por minuto
4. Se acredita 1 punto de lealtad por cada 10 minutos de sesión completada
5. Para canjear puntos se necesitan mínimo 500 puntos
6. El nivel de lealtad cambia automáticamente según el saldo de puntos
7. Las reservas no pueden tener conflicto de horario en la misma estación
8. Una estación en MAINTENANCE u OFFLINE no puede recibir sesiones
9. Si notification-service falla, la creación del torneo igual se completa (fallback)
10. El hardware-monitor alerta automáticamente si la temperatura supera los umbrales

## Endpoints Principales

### A través del API Gateway (puerto 8080)
GET http://localhost:8080/api/users
GET http://localhost:8080/api/stations
POST http://localhost:8080/api/sessions
PUT http://localhost:8080/api/sessions/{id}/finish
GET http://localhost:8080/api/v1/productos
GET http://localhost:8080/api/v1/reservas
GET http://localhost:8080/api/v1/billeteras
GET http://localhost:8080/api/v1/loyalty/{userId}
GET http://localhost:8080/api/v1/hardware/status
GET http://localhost:8080/api/v1/notifications/logs
GET http://localhost:8080/api/v1/tournaments

## Swagger UI por Microservicio

| Servicio | URL |
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


