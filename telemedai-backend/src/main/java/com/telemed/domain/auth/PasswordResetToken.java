package com.telemed.domain.auth;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

/**
 * Token de un solo uso para recuperación de contraseña.
 * 
 * Se genera cuando un usuario solicita restablecer su contraseña.
 * Tiene una fecha de expiración (30 minutos) y se marca como usado al aplicarlo.
 * El token se almacena como hash para mayor seguridad.
 */
@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuario asociado a este token.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Hash SHA-256 del token raw (se almacena en lugar del token en texto plano).
     */
    @Column(name = "token_hash", nullable = false, unique = true, length = 120)
    private String tokenHash;

    /**
     * Fecha y hora de expiración (30 minutos después de la creación).
     */
    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    /**
     * Indica si el token ya fue utilizado.
     */
    @Column(nullable = false)
    private boolean used;
}