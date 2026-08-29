package com.telemed.interfaces.auth;

import com.telemed.application.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para autenticación y gestión de usuarios.
 * 
 * Todos los endpoints de este controlador son públicos (no requieren autenticación).
 * Permite:
 * - Registro de nuevos pacientes.
 * - Login (obtención de tokens JWT).
 * - Renovación de access token usando refresh token.
 * - Solicitar recuperación de contraseña.
 * - Restablecer contraseña con token de recuperación.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    /**
     * Registra un nuevo paciente en el sistema.
     * Crea un User y un Patient asociado en la misma transacción.
     * 
     * @param r Datos del registro (nombre, email, documento, contraseña).
     * @return Tokens JWT (access y refresh).
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar paciente")
    public AuthResponse register(@Valid @RequestBody AuthRequest r) {
        var t = auth.register(r.fullName(), r.email(), r.identityDocument(), r.password(), r.phone(), r.birthDate());
        return new AuthResponse(t.accessToken(), t.refreshToken(), "Bearer", t.userId(), t.patientId(),  t.professionalId(),  t.role());
    }

    /**
     * Autentica un usuario con email y contraseña.
     * 
     * @param r Credenciales (email y password).
     * @return Tokens JWT (access y refresh).
     */
    @PostMapping("/login")
    @Operation(summary = "Autenticar usuario")
    public AuthResponse login(@Valid @RequestBody LoginRequest r) {
        var t = auth.login(r.email(), r.password());
        return new AuthResponse(t.accessToken(), t.refreshToken(), "Bearer", t.userId(),  t.patientId(), t.professionalId(), t.role());
    }

    /**
     * Renueva el access token usando un refresh token válido.
     * 
     * @param r Refresh token (obtenido en login o registro).
     * @return Nuevos tokens (access y refresh).
     */
    @PostMapping("/refresh")
    @Operation(summary = "Renovar access token")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest r) {
        var t = auth.refresh(r.refreshToken());
        return new AuthResponse(t.accessToken(), t.refreshToken(), "Bearer", t.userId(), t.patientId(), t.professionalId(), t.role());
    }

    /**
     * Solicita recuperación de contraseña.
     * Envía un token de un solo uso al correo registrado.
     * 
     * @param r Correo electrónico del usuario.
     */
    @PostMapping("/password-recovery")
    @Operation(summary = "Solicitar recuperación")
    public void recovery(@Valid @RequestBody PasswordRecoveryRequest r) {
        auth.requestPasswordRecovery(r.email());
    }

    /**
     * Restablece la contraseña usando un token de recuperación.
     * 
     * @param r Token y nueva contraseña.
     */
    @PostMapping("/password-reset")
    @Operation(summary = "Restablecer contraseña")
    public void reset(@Valid @RequestBody PasswordResetRequest r) {
        auth.resetPassword(r.token(), r.newPassword());
    }
}