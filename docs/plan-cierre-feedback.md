# Plan de Cierre y Feedback — Arena Gamer Platform

## Feedback Recibido (EV3)

### Fortalezas identificadas
- Las reglas de negocio son de las más sólidas de la evaluación
- El flujo de sesión que valida usuario y estación, calcula el costo y encadena el descuento de saldo con la acreditación de puntos está muy bien pensado
- La comunicación REST combina bien RestClient y OpenFeign con fallback
- La documentación Swagger es completa
- 7 de 10 servicios tienen pruebas unitarias reales

### Puntos a mejorar
- El api-gateway no tenía su propio Dockerfile ni estaba incluido en el docker-compose.yml
- Las rutas del Gateway apuntaban a localhost en vez de nombres de servicio Docker
- Faltaban filtros en el Gateway
- Falta una segunda plataforma de despliegue

## Correcciones Aplicadas (EV4)

| Problema | Solución aplicada | Estado |
|---|---|---|
| API Gateway sin Dockerfile | Creado Dockerfile con usuario no-root | ✅ Resuelto |
| Gateway fuera del compose.yml | Agregado al docker-compose con variables de entorno Docker | ✅ Resuelto |
| Rutas con localhost | Cambiadas a variables de entorno con nombres de servicio Docker | ✅ Resuelto |
| Sin filtros en Gateway | Agregados AddRequestHeader global y por ruta | ✅ Resuelto |
| Cobertura de pruebas < 80% | user-service subió a 82% con nuevos tests de DTO, exception y security | ✅ Resuelto |
| WalletClient/LoyaltyClient hardcodeados | Migrados a @Value con variables de entorno | ✅ Resuelto |
| application.yaml corrupto | Eliminado y consolidado en application.yml limpio | ✅ Resuelto |

## Estado Final del Sistema

- 10 microservicios operativos
- 1 API Gateway con filtros y containerización completa
- 11 contenedores Docker corriendo simultáneamente
- Comunicación inter-contenedor con nombres de servicio (no localhost)
- Cobertura de pruebas: 82% en user-service
- Swagger documentado en los 10 microservicios
- HATEOAS con links condicionales en los 10 microservicios