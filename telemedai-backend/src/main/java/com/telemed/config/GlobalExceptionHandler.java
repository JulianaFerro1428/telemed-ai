package com.telemed.config;

import com.telemed.shared.dto.ErrorResponse;
import com.telemed.shared.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

/**
 * Manejador global de excepciones para toda la aplicación.
 * 
 * Este componente captura todas las excepciones lanzadas por los controladores
 * y las transforma en respuestas JSON consistentes con el formato:
 * {
 *   "timestamp": "2026-08-26T...",
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Mensaje descriptivo del error",
 *   "path": "/api/endpoint"
 * }
 * 
 * Además, registra en los logs las excepciones internas (500) para facilitar la depuración.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones de recurso no encontrado (404).
     * 
     * @param e Excepción lanzada.
     * @param r Solicitud HTTP actual.
     * @return Respuesta HTTP 404 con el mensaje de error.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorResponse> notFound(ResourceNotFoundException e, HttpServletRequest r) {
        return build(HttpStatus.NOT_FOUND, e.getMessage(), r);
    }

    /**
     * Maneja excepciones de negocio y de argumentos inválidos (400).
     * 
     * @param e Excepción lanzada (DomainException, IllegalArgumentException, IllegalStateException).
     * @param r Solicitud HTTP actual.
     * @return Respuesta HTTP 400 con el mensaje de error.
     */
    @ExceptionHandler({DomainException.class, IllegalArgumentException.class, IllegalStateException.class})
    ResponseEntity<ErrorResponse> business(RuntimeException e, HttpServletRequest r) {
        return build(HttpStatus.BAD_REQUEST, e.getMessage(), r);
    }

    /**
     * Maneja errores de validación de Bean Validation (400).
     * 
     * Los campos con anotaciones @NotBlank, @NotNull, @Email, etc.
     * generan errores de validación que se recopilan en un solo mensaje.
     * 
     * @param e Excepción lanzada por Spring cuando falla la validación.
     * @param r Solicitud HTTP actual.
     * @return Respuesta HTTP 400 con el detalle de los campos que fallaron.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException e, HttpServletRequest r) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getField() + ": " + x.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, msg, r);
    }

    /**
     * Captura cualquier otra excepción no manejada (500).
     * 
     * Registra el error completo en los logs (stacktrace) y devuelve un mensaje genérico
     * al cliente para no exponer detalles internos.
     * 
     * @param e Excepción inesperada.
     * @param r Solicitud HTTP actual.
     * @return Respuesta HTTP 500 con un mensaje genérico.
     */
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> generic(Exception e, HttpServletRequest r) {
        // Log detallado con método, URL y stacktrace
        log.error("Error interno en {} {}", r.getMethod(), r.getRequestURI(), e);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor.", r);
    }

    /**
     * Construye una respuesta de error en formato JSON.
     * 
     * @param status Código HTTP de la respuesta.
     * @param message Mensaje descriptivo del error.
     * @param request Solicitud HTTP actual (para obtener la ruta).
     * @return ResponseEntity con el objeto ErrorResponse.
     */
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(
                        OffsetDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI()
                )
        );
    }
}