package com.telemed.shared.dto;

import java.time.OffsetDateTime;

/**
 * Formato estándar para respuestas de error HTTP.
 * 
 * Todas las excepciones capturadas por GlobalExceptionHandler se transforman
 * en este objeto para garantizar consistencia en las respuestas de error.
 * 
 * @param timestamp Fecha y hora del error (ISO 8601 con timezone).
 * @param status Código HTTP del error (ej. 400, 404, 500).
 * @param error Nombre del error (ej. "Bad Request", "Not Found").
 * @param message Mensaje descriptivo del error (puede ser técnico o amigable).
 * @param path Ruta del endpoint que generó el error.
 */
public record ErrorResponse(
    OffsetDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) {}