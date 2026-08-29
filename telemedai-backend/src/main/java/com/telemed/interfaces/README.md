# Capa de Interfaces (Interfaces)

## Propósito

Esta capa contiene los **controladores REST** que exponen la API de la plataforma. Es el punto de entrada para las solicitudes HTTP provenientes del frontend (aplicación móvil, panel web, etc.).

Cada controlador:

- Recibe peticiones HTTP (GET, POST, PUT, PATCH, DELETE).
- Valida los datos de entrada usando anotaciones de Bean Validation (`@Valid`).
- Delega la lógica de negocio a los servicios de la capa de aplicación.
- Devuelve respuestas JSON con los datos solicitados o mensajes de error.

## Estructura

| Subcarpeta | Contenido |
|------------|-----------|
| `appointment/` | Controlador para gestionar citas (crear, cancelar, reprogramar, consultar, etc.). |
| `auth/` | Controlador para autenticación y registro (login, register, refresh, recuperación). |
| `notification/` | Controlador para consultar y enviar notificaciones. |
| `patient/` | Controlador para gestionar el perfil del paciente. |
| `professional/` | Controlador para gestionar profesionales (listar, registrar, admin). |
| `summary/` | Controlador para crear y consultar resúmenes clínicos. |

## Principios aplicados

- **Validación**: Todos los request bodies se validan con anotaciones `@Valid` y `@NotNull`, `@NotBlank`, `@Email`, etc.
- **Autorización**: Se usa `@PreAuthorize` para controlar el acceso según el rol (PACIENTE, PROFESIONAL, ADMIN).
- **Documentación**: Los endpoints están documentados con `@Operation` de Swagger/OpenAPI.
- **DTOs**: Se usan records (DTOs) para los request y response, manteniendo la API desacoplada del dominio.
- **URIs RESTful**: Se siguen convenciones REST: `/api/recurso`, `{id}` en la URL, etc.

## Nota sobre `agent/`

El controlador del agente (`AgentController`) actualmente no está implementado (obviado en esta documentación). Se agregará cuando se implemente la IA real.