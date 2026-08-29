package com.telemed.application.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Servicio para generar y validar tokens JWT.
 * 
 * Utiliza la librería JJWT para:
 * - Generar access tokens (1h de duración) y refresh tokens (7d).
 * - Firmar con algoritmo HS256 y clave secreta (configurable).
 * - Validar la firma y extraer el "subject" (email del usuario).
 */
@Service
public class JwtService {
    private final SecretKey key;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtService(
            @Value("${telemed.jwt.secret}") String secret,
            @Value("${telemed.jwt.access-expiration-ms}") long accessExpiration,
            @Value("${telemed.jwt.refresh-expiration-ms}") long refreshExpiration) {
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET debe tener al menos 32 caracteres.");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    /**
     * Genera un token de acceso con el rol del usuario.
     */
    public String generateAccessToken(String email, String role) {
        return generate(email, role, accessExpiration);
    }

    /**
     * Genera un refresh token (sin rol, solo subject).
     */
    public String generateRefreshToken(String email) {
        return generate(email, null, refreshExpiration);
    }

    /**
     * Método privado para construir y firmar el token.
     */
    private String generate(String subject, String role, long expiration) {
        var builder = Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration));
        if (role != null) {
            builder.claim("role", role);
        }
        return builder.signWith(key).compact();
    }

    /**
     * Extrae el email (subject) de un token válido.
     */
    public String extractUsername(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Valida que el token esté correctamente firmado y no haya expirado.
     */
    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}