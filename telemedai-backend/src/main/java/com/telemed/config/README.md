# Capa de Configuración (Config)

## Propósito

Esta capa contiene todas las configuraciones globales de la aplicación:

- **Seguridad**: Configuración de Spring Security, CORS, filtros JWT.
- **Manejo de errores**: Manejador global de excepciones para respuestas JSON consistentes.
- **Tareas programadas**: Configuración para tareas asíncronas o programadas.

## Estructura

| Archivo | Responsabilidad |
|---------|-----------------|
| `GlobalExceptionHandler.java` | Captura y maneja todas las excepciones no controladas, devolviendo respuestas JSON estructuradas. |
| `SecurityConfig.java` | Configura la seguridad de la aplicación: autenticación stateless con JWT, autorización por roles, CORS. |
| `WebConfig.java` | Configuración general de la web: tareas programadas y otras configuraciones de Spring MVC. |

## Principios aplicados

- **Centralización**: Todas las configuraciones críticas están en un solo lugar.
- **Stateless**: La aplicación no mantiene estado de sesión (usando JWT).
- **Manejo de errores consistente**: Todas las excepciones se transforman en un formato JSON estándar.