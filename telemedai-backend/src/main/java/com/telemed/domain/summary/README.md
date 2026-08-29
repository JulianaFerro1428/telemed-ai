# Capa de Dominio (Domain)

## Propósito

Esta capa contiene el **modelo del negocio** de la plataforma de telemedicina. Es el corazón de la aplicación y **no depende de ninguna tecnología externa** (bases de datos, frameworks, etc.). Representa las entidades, objetos de valor, agregados y reglas de negocio.

## Estructura

| Subcarpeta | Contenido |
|------------|-----------|
| `agent/` | Entidades para la preconsulta conversacional (Agente) – **pendiente de implementación con IA real**. |
| `appointment/` | Agregado `Appointment`, su estado y la agenda del profesional. |
| `auth/` | Entidades de autenticación: `User`, `Role`, `RefreshToken`, `PasswordResetToken`. |
| `notification/` | Entidad `Notification` para mensajes al usuario. |
| `patient/` | Perfil del paciente y su historial médico (Value Object). |
| `professional/` | Perfil del profesional y su especialidad. |
| `summary/` | Resúmenes clínicos: preconsulta, atención y post (combinación). |

## Principios aplicados

- **Entidades con identidad**: `User`, `Patient`, `Professional`, `Appointment`, etc.
- **Objetos de valor**: `MedicalHistory` (inmutable, sin identidad).
- **Agregados**: `Appointment` es un agregado con sus propias reglas de negocio (máquina de estados).
- **Invariantes**: Se validan en los constructores y métodos de dominio (ej. no reprogramar una cita cancelada).
- **Enumerados**: `AppointmentStatus` para estados finitos del ciclo de vida.

## Nota sobre `agent/`

El agente conversacional actualmente es **simulado** (sin IA real). Está en la capa de aplicación (`AgentService`) y solo usa las entidades `Conversation` y `Message` (que se espera que estén en `domain.agent`). Esta carpeta está pendiente de implementación completa.