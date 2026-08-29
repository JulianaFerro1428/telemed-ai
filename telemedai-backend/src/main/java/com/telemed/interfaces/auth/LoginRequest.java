package com.telemed.interfaces.auth;

import jakarta.validation.constraints.*;

/**
 * DTO para la solicitud de login.
 */
public record LoginRequest(
    @NotBlank @Email String email,
    @NotBlank String password
) {}