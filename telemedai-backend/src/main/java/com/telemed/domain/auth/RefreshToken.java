package com.telemed.domain.auth;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

/**
 * Refresh token persistente para renovar el access token.
 * 
 * Se almacena como hash (no en texto plano), tiene fecha de expiración (7 días)
 * y puede ser revocado (por ejemplo, al cambiar la contraseña).
 * Cada vez que se usa se rota (se crea uno nuevo y se revoca el anterior).
 */
@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token_hash", nullable = false, unique = true, length = 120)
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked;
}