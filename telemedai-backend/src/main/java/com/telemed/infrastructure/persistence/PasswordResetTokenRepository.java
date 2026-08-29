package com.telemed.infrastructure.persistence;

import com.telemed.domain.auth.PasswordResetToken;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad PasswordResetToken (token de recuperación).
 * 
 * Maneja tokens de un solo uso para restablecer contraseñas.
 * Proporciona métodos para buscar un token por su hash y revocar tokens activos.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    /**
     * Busca un token por su hash.
     * 
     * @param tokenHash Hash SHA-256 del token.
     * @return Optional con el token si existe.
     */
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    /**
     * Revoca todos los tokens activos de un usuario.
     * 
     * Marca como usados todos los tokens que no han sido utilizados.
     * Esto ocurre cuando se solicita un nuevo token o se cambia la contraseña.
     * 
     * @param userId ID del usuario.
     * @return Número de tokens revocados.
     */
    @Modifying
    @Query("update PasswordResetToken p set p.used=true where p.user.id=:userId and p.used=false")
    int revokeActiveTokens(@Param("userId") Long userId);
}