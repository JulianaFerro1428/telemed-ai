package com.telemed.interfaces.notification;

import com.telemed.application.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar notificaciones de los usuarios.
 * 
 * Permite:
 * - Listar notificaciones de un usuario (autenticado).
 * - Enviar una notificación (solo administradores).
 * 
 * Todas las operaciones requieren autenticación.
 */
@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("isAuthenticated()")
public class NotificationController {
    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    /**
     * DTO para enviar una notificación (solo admin).
     */
    public record Request(
        @NotNull Long userId,
        @NotBlank String type,
        @NotBlank String message
    ) {}

    /**
     * Lista todas las notificaciones de un usuario.
     * 
     * @param id ID del usuario.
     * @return Lista de notificaciones.
     */
    @GetMapping("/user/{id}")
    public Object list(@PathVariable Long id) {
        return service.byUser(id);
    }

    /**
     * Envía una notificación a un usuario (solo administradores).
     * 
     * @param r Datos de la notificación (userId, type, message).
     * @return La notificación creada.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enviar notificación")
    public Object send(@Valid @RequestBody Request r) {
        return service.send(r.userId(), r.type(), r.message());
    }
}