package com.telemed.interfaces.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para renovar el access token usando un refresh token.
 */
public record RefreshRequest(
    @NotBlank String refreshToken
) {}