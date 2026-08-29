# Capa de Aplicación (Application)

## Propósito

Esta capa contiene los **casos de uso** del sistema. Es el punto de entrada para las operaciones de negocio orquestadas por los controladores (capa de interfaces).

Cada clase en esta carpeta:

- **Implementa la lógica de negocio** usando entidades y objetos de valor del dominio.
- **Coordina** las operaciones entre diferentes agregados y repositorios.
- **Gestiona transacciones** mediante `@Transactional`.
- **Aplica reglas de negocio** (validaciones, máquinas de estado, etc.).
- **Comunica eventos** o efectos secundarios (como envío de correos simulados).

## Estructura

| Servicio | Responsabilidad |
|----------|----------------|
| `AgentService` | Gestiona la conversación del agente inteligente (preconsulta simulada). |
| `AppointmentService` | Gestiona el ciclo de vida de las citas (crear, cancelar, reprogramar, cambiar estado). |
| `AuthService` | Maneja registro, login, refresh token y recuperación de contraseña. |
| `JwtService` | Genera y valida tokens JWT. |
| `JwtAuthenticationFilter` | Filtro de seguridad que autentica solicitudes con JWT. |
| `NotificationService` | Envía notificaciones y correos simulados. |
| `PatientService` | Gestiona el perfil del paciente (consultar, actualizar). |
| `ProfessionalService` | Gestiona el registro y consulta de profesionales (solo admin). |
| `SummaryService` | Gestiona resúmenes clínicos (preconsulta, atención, post). |
| `PasswordRecoveryService` | (Pendiente) Recuperación de contraseña. |

## Principios aplicados

- **Separación de responsabilidades**: Cada servicio maneja un contexto delimitado.
- **Transaccionalidad**: Todas las operaciones que modifican el estado usan `@Transactional`.
- **Inyección de dependencias**: Los repositorios y servicios auxiliares se inyectan por constructor.
- **Manejo de excepciones**: Se lanzan excepciones de dominio (`DomainException`) o de recurso no encontrado (`ResourceNotFoundException`).