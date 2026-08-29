package com.telemed.domain.notification;

import com.telemed.domain.auth.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

/**
 * Notificación persistida para un usuario.
 * 
 * Representa un mensaje informativo sobre eventos del sistema
 * (confirmación de cita, cancelación, recordatorio, etc.).
 * 
 * Se almacena en la base de datos y se puede consultar desde la API.
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Tipo de notificación (ej. "CITA_CONFIRMADA", "CITA_CANCELADA").
     */
    @Column(nullable = false, length = 40)
    private String type;

    /**
     * Contenido de la notificación.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * Indica si el usuario ya ha leído la notificación.
     */
    @Column(nullable = false)
    private boolean read;

    /**
     * Fecha de envío.
     */
    @Column(name = "sent_at", nullable = false)
    private OffsetDateTime sentAt;

    /**
     * Constructor que crea una notificación no leída con la fecha actual.
     * 
     * @param user Usuario destinatario.
     * @param type Tipo de notificación.
     * @param message Mensaje.
     */
    public Notification(User user, String type, String message) {
        this.user = user;
        this.type = type;
        this.message = message;
        this.read = false;
        this.sentAt = OffsetDateTime.now();
    }
}