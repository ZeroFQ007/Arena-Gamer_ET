# Matriz de Requerimientos — Arena Gamer Platform

## Requerimientos Funcionales

| ID | Requerimiento | Microservicio | Estado |
|---|---|---|---|
| RF-01 | Registrar usuarios con email y contraseña | user-service | ✅ Implementado |
| RF-02 | Autenticar usuarios (HTTP Basic) | user-service | ✅ Implementado |
| RF-03 | Gestionar estaciones PC y CONSOLE | station-service | ✅ Implementado |
| RF-04 | Iniciar sesión de juego validando usuario y estación | session-service | ✅ Implementado |
| RF-05 | Finalizar sesión calculando costo y acreditando puntos | session-service | ✅ Implementado |
| RF-06 | Cancelar sesión activa | session-service | ✅ Implementado |
| RF-07 | Gestionar inventario de productos alquilables | arena-inventory | ✅ Implementado |
| RF-08 | Crear y gestionar reservas de estaciones | arena-reservas | ✅ Implementado |
| RF-09 | Validar conflictos de horario en reservas | arena-reservas | ✅ Implementado |
| RF-10 | Gestionar billeteras virtuales por usuario | arena-wallet | ✅ Implementado |
| RF-11 | Recargar y descontar saldo de billetera | arena-wallet | ✅ Implementado |
| RF-12 | Monitorear temperatura CPU/GPU de equipos | hardware-monitor | ✅ Implementado |
| RF-13 | Alertar cuando temperatura supera umbral | hardware-monitor | ✅ Implementado |
| RF-14 | Gestionar puntos de fidelización por sesión | loyalty-service | ✅ Implementado |
| RF-15 | Canjear puntos por premios | loyalty-service | ✅ Implementado |
| RF-16 | Registrar y consultar notificaciones | notification-service | ✅ Implementado |
| RF-17 | Crear y listar torneos competitivos | tournament-service | ✅ Implementado |
| RF-18 | Enrutar requests a través de API Gateway | api-gateway | ✅ Implementado |

## Requerimientos No Funcionales

| ID | Requerimiento | Estado |
|---|---|---|
| RNF-01 | Arquitectura de microservicios independientes | ✅ Implementado |
| RNF-02 | Documentación Swagger en cada microservicio | ✅ Implementado |
| RNF-03 | Pruebas unitarias con cobertura mínima 80% | ✅ 82% en user-service |
| RNF-04 | Contenedores Docker con usuario no-root | ✅ Implementado |
| RNF-05 | Respuestas JSON con enlaces HATEOAS | ✅ Implementado |
| RNF-06 | Validación de datos con Bean Validation | ✅ Implementado |
| RNF-07 | Manejo de excepciones con @ControllerAdvice | ✅ Implementado |
| RNF-08 | Perfiles de configuración (h2/mysql) | ✅ Implementado |
| RNF-09 | Comunicación inter-servicio con fallback | ✅ Implementado |
| RNF-10 | API Gateway con filtros de trazabilidad | ✅ Implementado |