Defensa Individual — Ali Simancas
Información Personal
Nombre: Ali Simancas
GitHub: [completar usuario]
Curso: DSY1103 Desarrollo FullStack 1
Equipo: N°12
Microservicios bajo mi responsabilidad
Servicio	Puerto	Descripción
hardware-monitor	8090	Monitoreo de temperatura CPU/GPU de las estaciones
loyalty-service	8087	Puntos de fidelización de clientes
notification-service	8089	Registro y envío de notificaciones
tournament-service	8088	Gestión de torneos competitivos
arena-wallet	8085	Billeteras virtuales y transacciones
arena-reservas	9000	Reservas de estaciones con historial
arena-inventory	9001	Inventario de productos alquilables
Infraestructura y despliegue (contribución adicional al equipo)
Además de mis 7 microservicios, tomé la deuda técnica de infraestructura marcada por el profesor:
Aporte	Descripción
`eureka-server`	Módulo nuevo desde cero — Spring Cloud Netflix Eureka Server, `@EnableEurekaServer`, puerto 8761, self-preservation desactivado para entorno académico
Eureka Client	Configuración de `spring-cloud-starter-netflix-eureka-client` y bloque `eureka.client` en los microservicios del equipo
`GlobalErrorFilter`	Filtro funcional (`GlobalFilter`) agregado al api-gateway para auditoría de requests/responses y logging de errores — antes solo había filtros cosméticos de headers
Rutas dinámicas del Gateway	Migración de `application.yml` del api-gateway de URLs estáticas Docker a rutas `lb://` resueltas por Eureka
`render.yaml`	Blueprint IaC para desplegar eureka-server, user-service y api-gateway en Render (segunda plataforma exigida por la pauta)
`docker-compose.yml`	Actualización para integrar eureka-server con healthcheck y `EUREKA_URI` en todos los servicios
Commits destacados
> Nota: mensajes redactados según el trabajo real hecho — verifica contra tu `git log` y ajusta el texto exacto/hashes antes de entregar.
`feat: crear módulo eureka-server con Spring Cloud Netflix Eureka Server`
`feat: agregar GlobalErrorFilter al api-gateway para auditoría y manejo de errores`
`feat: migrar rutas del api-gateway de URLs estáticas a lb:// vía Eureka`
`feat: crear render.yaml para despliegue de eureka-server, user-service y api-gateway`
`feat: integrar eureka-server en docker-compose.yml con healthcheck`
`feat: migrar persistencia de arena-wallet, arena-reservas y arena-inventory de HashMap a JPA con H2`
`feat: implementar patrón DTO en arena-wallet, arena-reservas y arena-inventory`
`fix: resolver incompatibilidad de Spring Cloud/Feign con Spring Boot usando RestClient nativo`
`feat: agregar HATEOAS a hardware-monitor, loyalty-service, notification-service y tournament-service`
`refactor: migrar logging a SLF4J en mis 4 microservicios principales`
`test: agregar pruebas unitarias JUnit5/Mockito en mis servicios`
Clases que puedo explicar
hardware-monitor
`HardwareController` — endpoints REST de lecturas de temperatura por estación
`HardwareService` — lógica de negocio: registrar lectura, evaluar umbral de alerta
`HardwareRepository` — JPA con consultas por estación y rango de fecha
`HardwareReading` — entidad JPA (temperatura CPU/GPU, timestamp, estación asociada)
loyalty-service
`LoyaltyController` — endpoints REST de consulta y acreditación de puntos
`LoyaltyService` — lógica de negocio: acreditar puntos al finalizar sesión, consultar saldo de puntos
`LoyaltyRepository` — JPA con findByUserId
`LoyaltyAccount` — entidad JPA (usuario, puntos acumulados)
notification-service
`NotificationController` — endpoints REST de registro y consulta de notificaciones
`NotificationService` — lógica de negocio: registrar evento, marcar como leída
`NotificationRepository` — JPA con findByUserId, findByReadFalse
`Notification` — entidad JPA (tipo, mensaje, estado leído/no leído)
tournament-service
`TournamentController` — endpoints REST de creación, inscripción y resultados de torneos
`TournamentService` — lógica de negocio: crear torneo, inscribir jugador, cerrar torneo
`TournamentRepository` — JPA con findByStatus
`Tournament` — entidad JPA con enum de estado (OPEN/CLOSED/FINISHED)
arena-wallet
`WalletController` — endpoints REST de consulta de saldo y transacciones
`WalletService` — lógica de negocio: crear billetera, descontar saldo, validar fondos suficientes
`WalletRepository` — JPA con findByUserId
`Wallet` / `Transaction` — entidades JPA para saldo e historial de movimientos
arena-reservas
`ReservaController` — endpoints REST de creación y consulta de reservas
`ReservaService` — lógica de negocio: crear reserva, validar disponibilidad, cancelar
`ReservaRepository` — JPA con findByEstacionId, findByUsuarioId
`Reserva` — entidad JPA con estado y rango horario
arena-inventory
`ProductoController` — endpoints REST CRUD de productos alquilables
`ProductoService` — lógica de negocio: crear producto, actualizar stock
`ProductoRepository` — JPA con findByDisponibleTrue
`Producto` — entidad JPA (nombre, stock, precio)
eureka-server
`EurekaServerApplication` — clase principal con `@EnableEurekaServer`
`application.yml` — configuración de `register-with-eureka: false`, `fetch-registry: false`, self-preservation
api-gateway (aporte específico)
`GlobalErrorFilter` — filtro global (`GlobalFilter`, `Ordered`) que audita cada request/response y loguea errores del pipeline reactivo
`application.yml` — rutas `lb://` resueltas por Eureka y bloque `eureka.client`
Lógica de negocio que puedo explicar
Cierre del ciclo de sesión (mis servicios participan al final del flujo)
session-service finaliza la sesión y calcula el costo
arena-wallet descuenta el saldo de la billetera del usuario
loyalty-service acredita los puntos de fidelización correspondientes
notification-service registra la notificación de cierre de sesión al usuario
Monitoreo de hardware
hardware-monitor recibe/registra lecturas periódicas de temperatura CPU/GPU por estación y puede marcar alertas si se supera un umbral definido.
Torneos
tournament-service gestiona el ciclo de vida de un torneo: creación, inscripción de jugadores, cierre y registro de resultados.
Inventario, reservas y billeteras (servicios que también trabajé)
arena-inventory controla stock de productos alquilables (periféricos, sillas, etc.).
arena-reservas gestiona la reserva de estaciones con validación de disponibilidad y solapamiento horario.
arena-wallet administra saldo y transacciones de cada usuario. Estos tres servicios fueron migrados desde persistencia en memoria (HashMap) a JPA con H2/MySQL, incorporando el patrón DTO para no exponer las entidades directamente.
Service discovery con Eureka
eureka-server centraliza el registro de instancias; cada microservicio se registra con `spring-cloud-starter-netflix-eureka-client` apuntando a `EUREKA_URI`.
El api-gateway resuelve sus rutas dinámicamente contra Eureka (`lb://NOMBRE-SERVICIO`) en vez de URLs fijas, lo que permite escalar instancias sin tocar configuración del Gateway.
Filtro global del Gateway
`GlobalErrorFilter` intercepta cada request antes y después de pasar por la cadena de filtros (`GatewayFilterChain`), logueando método, path, IP de origen, duración y, si corresponde, el error capturado — cumple con la exigencia de "filtros funcionales" más allá de simples headers.
Despliegue en Render
`render.yaml` define como Blueprint (Infrastructure as Code) el despliegue Docker de eureka-server, user-service y api-gateway como segunda plataforma de despliegue, cumpliendo el requisito de la pauta.
Pruebas unitarias
Servicio	Archivos de test
arena-inventory	3
arena-wallet	3
loyalty-service	2
arena-reservas	2
hardware-monitor	1
notification-service	1
tournament-service	1
> Nota: hardware-monitor, notification-service y tournament-service quedaron con cobertura débil (1 archivo cada uno) — pendiente reforzar antes de la entrega final. No tengo el % de cobertura exacto (JaCoCo); corre `mvnw test` + reporte JaCoCo por servicio para completar esta tabla con datos reales.
Feedback corregido personalmente
Observación del profesor	Qué hice
Gateway no tenía Dockerfile ni estaba en docker-compose	Verifiqué que el Dockerfile ya existía y aseguré su integración correcta en `docker-compose.yml` junto con eureka-server
Rutas del Gateway a `localhost` en vez de contenedores Docker	Migré las rutas a `lb://` resueltas por Eureka, eliminando la dependencia de URLs fijas
Faltaban filtros funcionales en el Gateway	Implementé `GlobalErrorFilter` para auditoría y manejo de errores
Faltaba segunda plataforma de despliegue (Render)	Creé `render.yaml` como Blueprint IaC para eureka-server, user-service y api-gateway
Eureka Server no existía (obligatorio en la pauta)	Creé el módulo completo desde cero e integré el cliente en los microservicios del equipo
Dificultad personal
[Completa aquí en primera persona: qué fue lo más difícil de este bloque de trabajo — ej. resolver la resolución dinámica de rutas del Gateway con Eureka, o decidir cómo estructurar `render.yaml` sin poder probarlo en un despliegue real antes de la entrega.]
Checklist individual
[ ] Verificar que mis 7 microservicios registran correctamente en Eureka (`http://localhost:8761`)
[ ] Confirmar que el Gateway resuelve las rutas `lb://` en Docker
[ ] Completar `EUREKA_URI` real en Render tras el primer deploy
[ ] Reforzar tests en hardware-monitor, notification-service y tournament-service
[ ] Actualizar `plan-cierre-feedback.md` con la tabla de la sección "Feedback corregido personalmente"
[ ] Completar mi usuario de GitHub y datos pendientes marcados con `[completar]`