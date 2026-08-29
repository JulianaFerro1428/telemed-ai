package com.telemed.shared.exception;

/**
 * Excepción lanzada cuando se viola una regla de negocio.
 * 
 * Esta excepción se usa en la capa de dominio y aplicación para indicar
 * que una operación no puede completarse debido a una restricción del negocio.
 * 
 * Ejemplos:
 * - "El correo ya está registrado."
 * - "El profesional no está disponible en ese horario."
 * - "No se puede cancelar una cita en estado COMPLETADA."
 * 
 * Es manejada por GlobalExceptionHandler y devuelve un 400 Bad Request.
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}