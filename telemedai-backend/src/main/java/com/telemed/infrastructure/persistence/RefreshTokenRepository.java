package com.telemed.infrastructure.persistence;

import com.telemed.domain.auth.RefreshToken;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad RefreshToken.
 * 
 * Maneja tokens de refresco para renovar sesiones.
 * Proporciona métodos para buscar por hash y revocar tokens de un usuario.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    /**
     * Busca un refresh token por su hash.
     * 
     * @param tokenHash Hash SHA-256 del token.
     * @return Optional con el token si existe.
     */
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    /**
     * Revoca todos los refresh tokens activos de un usuario.
     * 
     * Se usa al cambiar la contraseña o al cerrar sesión en todos los dispositivos.
     * 
     * @param userId ID del usuario.
     * @return Número de tokens revocados.
     */
    @Modifying
    @Query("update RefreshToken r set r.revoked=true where r.user.id=:userId and r.revoked=false")
    int revokeAllByUserId(@Param("userId") Long userId);
}