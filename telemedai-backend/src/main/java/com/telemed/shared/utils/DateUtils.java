package com.telemed.shared.utils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Utilidades centralizadas para el manejo de fechas.
 * 
 * Esta clase proporciona métodos estáticos para operaciones comunes con fechas,
 * garantizando consistencia en toda la aplicación (ej. uso de UTC para todas las fechas).
 * 
 * Todas las fechas en la aplicación se almacenan en UTC (Zona horaria 0).
 * Esto evita problemas de conversión entre diferentes zonas horarias.
 */
public final class DateUtils {
    /**
     * Constructor privado para evitar instanciación (clase de utilidad).
     */
    private DateUtils() {}

    /**
     * Obtiene la fecha y hora actual en UTC.
     * 
     * @return OffsetDateTime con la hora actual en UTC (zona horaria 0).
     */
    public static OffsetDateTime nowUtc() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}