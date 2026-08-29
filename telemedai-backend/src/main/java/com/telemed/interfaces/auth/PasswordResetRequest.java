package com.telemed.interfaces.auth;

import jakarta.validation.constraints.*;

/**
 * DTO para restablecer la contraseña con token de recuperación.
 */
public record PasswordResetRequest(
    @NotBlank String token,
    @NotBlank @Size(min = 8, max = 100) String newPassword
) {}