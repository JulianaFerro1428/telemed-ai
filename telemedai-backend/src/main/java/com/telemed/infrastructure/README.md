# Capa de Infraestructura (Infrastructure)

## Propósito

Esta capa contiene los **detalles técnicos** de la aplicación que permiten que el dominio se comunique con el mundo exterior:

- **Persistencia**: Repositorios JPA para acceder a la base de datos.
- **Comunicación externa**: Adaptadores para enviar correos electrónicos y SMS (actualmente simulados).
- **Preparado para integración futura**: Los adaptadores de correo y SMS están diseñados para ser reemplazados por implementaciones reales (SMTP, SendGrid, Twilio, etc.).

## Estructura

| Subcarpeta | Contenido |
|------------|-----------|
| `email/` | Adaptador para envío de correos electrónicos (simulado). |
| `persistence/` | Repositorios JPA para cada entidad del dominio. |
| `sms/` | Adaptador para envío de SMS (simulado). |

## Principios aplicados

- **Inversión de dependencias**: La capa de aplicación depende de interfaces, no de implementaciones concretas.
- **Simulación**: Los adaptadores de correo y SMS son simulados (loguean en consola) para no depender de servicios externos en desarrollo.
- **Repositorios**: Cada agregado/entidad tiene su propio repositorio JPA.
- **Métodos personalizados**: Los repositorios incluyen métodos específicos para consultas comunes (ej. `existsByProfessionalIdAndStatusIn...`).