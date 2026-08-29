package com.telemed.shared.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no existe en la base de datos.
 * 
 * Se usa cuando una entidad (Usuario, Paciente, Cita, etc.) no se encuentra
 * por su ID o por algún criterio de búsqueda.
 * 
 * Ejemplos:
 * - "Paciente no encontrado."
 * - "Cita no encontrada."
 * - "Especialidad no encontrada."
 * 
 * Es manejada por GlobalExceptionHandler y devuelve un 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}