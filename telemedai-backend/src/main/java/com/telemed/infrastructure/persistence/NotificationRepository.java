package com.telemed.infrastructure.persistence;

import com.telemed.domain.notification.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA para la entidad Notification (notificaciones).
 * 
 * Persiste las notificaciones enviadas a los usuarios.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /**
     * Lista todas las notificaciones de un usuario, ordenadas por fecha descendente.
     * 
     * @param userId ID del usuario.
     * @return Lista de notificaciones del usuario.
     */
    List<Notification> findByUserIdOrderBySentAtDesc(Long userId);
}