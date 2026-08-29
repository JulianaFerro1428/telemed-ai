package com.telemed.interfaces.auth;

import jakarta.validation.constraints.*;

/**
 * DTO para solicitar recuperación de contraseña.
 */
public record PasswordRecoveryRequest(
    @NotBlank @Email String email
) {}