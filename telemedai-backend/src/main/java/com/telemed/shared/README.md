# Capa Compartida (Shared)

## Propósito

Esta capa contiene código que se utiliza en **todas las demás capas** de la aplicación. Son componentes transversales que no pertenecen a una capa específica:

- **DTOs (Data Transfer Objects)**: Objetos para transferir datos entre capas (ej. solicitudes y respuestas HTTP).
- **Excepciones personalizadas**: Clases de excepción para manejar errores específicos del dominio.
- **Utilidades**: Funciones auxiliares reutilizables (ej. manejo de fechas).

## Estructura

| Subcarpeta | Contenido |
|------------|-----------|
| `dto/` | Objetos de transferencia de datos. |
| `exception/` | Excepciones personalizadas del negocio. |
| `utils/` | Utilidades reutilizables. |

## Principios aplicados

- **Reutilización**: El código compartido evita duplicación en toda la aplicación.
- **Estandarización**: Las DTOs definen un formato consistente para las respuestas y solicitudes de la API.
- **Separación de responsabilidades**: Las excepciones personalizadas permiten manejar errores de negocio de forma clara.
- **Inmutabilidad**: Las DTOs son records (inmutables) para garantizar consistencia.

## Contenido

### DTOs
- `ErrorResponse`: Formato estándar para respuestas de error HTTP (timestamp, status, error, message, path).
- `ProfessionalRequest`: DTO para registrar profesionales (con validaciones Bean Validation).

### Excepciones
- `DomainException`: Excepción lanzada cuando se viola una regla de negocio (400 Bad Request).
- `ResourceNotFoundException`: Excepción lanzada cuando un recurso solicitado no existe (404 Not Found).

### Utilidades
- `DateUtils`: Utilidad centralizada para manejo de fechas (ej. `nowUtc()`).