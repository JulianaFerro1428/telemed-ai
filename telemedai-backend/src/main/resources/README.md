# Recursos de la Aplicación (Resources)

## Propósito

Esta carpeta contiene todos los archivos de configuración y recursos estáticos necesarios para el funcionamiento de la aplicación:

- **Configuración de Spring Boot**: Archivos `application.yml`, `application-dev.yml`, `application-prod.yml`.
- **Migraciones de base de datos (Flyway)**: Scripts SQL en `db/migration/` que definen el esquema de la base de datos y los datos iniciales.
- **Recursos estáticos**: (si los hubiera) archivos HTML, CSS, imágenes, etc.

## Estructura

| Carpeta/Archivo | Descripción |
|-----------------|-------------|
| `application.yml` | Configuración principal de Spring Boot (base de datos, JPA, Flyway, JWT, etc.). |
| `application-dev.yml` | Configuración específica para el perfil `dev` (desarrollo). |
| `application-prod.yml` | Configuración específica para el perfil `prod` (producción). |
| `db/migration/` | Scripts SQL de Flyway para crear y actualizar el esquema de la base de datos. |

## Principios aplicados

- **Configuración externalizada**: Las propiedades sensibles se inyectan mediante variables de entorno (`.env`).
- **Perfiles de entorno**: Separación de configuraciones para desarrollo, pruebas y producción.
- **Migraciones versionadas**: Flyway gestiona el esquema de la base de datos con scripts SQL versionados.
- **Seguridad**: Las credenciales de la base de datos y secretos JWT no están en texto plano en el repositorio.

## Uso

1. **Entorno local**: Usa el perfil `dev` por defecto (configurado en `application.yml`).
2. **Producción**: Activa el perfil `prod` mediante la variable `SPRING_PROFILES_ACTIVE=prod`.
3. **Base de datos**: Los scripts de migración se ejecutan automáticamente al iniciar la aplicación.
4. **Variables de entorno**: Define las siguientes variables en un archivo `.env`:

```env
DB_URL=jdbc:postgresql://localhost:5432/telemed
DB_USERNAME=telemed
DB_PASSWORD=telemed
JWT_SECRET=una-clave-secreta-muy-larga-de-al-menos-32-caracteres
CORS_ORIGINS=http://localhost:4200
PORT=8080