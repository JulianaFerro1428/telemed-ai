package com.telemed.application.auth;

import org.springframework.stereotype.Service;

/** Punto de extensión para recuperación de contraseña mediante token de un solo uso. */
@Service
public class PasswordRecoveryService {
    public void requestRecovery(String email) {
        // En una integración real se generaría un token de recuperación con expiración.
    }
}
