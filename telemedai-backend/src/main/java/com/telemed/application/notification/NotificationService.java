package com.telemed.application.notification;

import com.telemed.domain.notification.Notification;
import com.telemed.infrastructure.email.EmailSender;
import com.telemed.infrastructure.persistence.*;
import com.telemed.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Servicio para gestionar notificaciones y correos simulados.
 * 
 * Permite enviar notificaciones a usuarios (persistiéndolas en BD)
 * y simular el envío de correos electrónicos (log en consola).
 */
@Service
public class NotificationService {
    private final NotificationRepository notifications;
    private final UserRepository users;
    private final EmailSender email;

    public NotificationService(NotificationRepository n, UserRepository u, EmailSender e) {
        this.notifications = n;
        this.users = u;
        this.email = e;
    }

    /**
     * Envía una notificación a un usuario (guarda en BD + correo simulado).
     */
    @Transactional
    public Notification send(Long userId, String type, String message) {
        var user = users.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado."));

        var n = notifications.save(new Notification(user, type, message));
        email.send(user.getEmail(), type, message);
        return n;
    }

    /**
     * Obtiene todas las notificaciones de un usuario, ordenadas por fecha descendente.
     */
    public List<Notification> byUser(Long userId) {
        return notifications.findByUserIdOrderBySentAtDesc(userId);
    }
}