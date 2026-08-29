package com.telemed.interfaces.auth;

/**
 * DTO para la respuesta de autenticación (login y registro).
 * 
 * Devuelve los tokens JWT y el tipo de token (Bearer).
 */
public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long userId,
    Long patientId,
    Long professionalId,
    String role
) {}