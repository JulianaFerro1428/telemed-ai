package com.telemed.application.auth;

import com.telemed.domain.auth.*;
import com.telemed.domain.patient.*;
import com.telemed.domain.professional.Professional;
import com.telemed.infrastructure.email.EmailSender;
import com.telemed.infrastructure.persistence.*;
import com.telemed.shared.exception.DomainException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * Servicio principal de autenticación y gestión de usuarios.
 * 
 * Implementa los casos de uso críticos:
 * - Registro de pacientes (crea User + Patient en una misma transacción).
 * - Login y generación de tokens (access + refresh).
 * - Refresh token (rota y revoca el anterior).
 * - Solicitud de recuperación de contraseña (envía token simulado).
 * - Restablecimiento de contraseña (hash y revoca sesiones activas).
 */
@Service
public class AuthService {
    private final UserRepository users;
    private final RoleRepository roles;
    private final PatientRepository patients;
    private final ProfessionalRepository professionals;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordResetTokenRepository resetTokens;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final EmailSender email;

    public AuthService(UserRepository u, RoleRepository r, PatientRepository p,
                       ProfessionalRepository prof, RefreshTokenRepository rt, PasswordResetTokenRepository pr,
                       PasswordEncoder e, JwtService j, EmailSender es) {
        users = u;
        roles = r;
        patients = p;
        professionals = prof;
        refreshTokens = rt;
        resetTokens = pr;
        encoder = e;
        jwt = j;
        email = es;
    }

    /**
     * Registra un nuevo paciente en el sistema.
     * 
     * @param name Nombre completo del paciente.
     * @param emailAddress Correo electrónico (único).
     * @param doc Documento de identidad (único).
     * @param password Contraseña en texto plano (se hashea con bcrypt).
     * @return Par de tokens (access + refresh).
     * @throws DomainException si el correo o documento ya están registrados.
     */
    @Transactional
    public TokenPair register(String name, String emailAddress, String doc, String password, String phone, LocalDate birthDate) {
        if (users.existsByEmailIgnoreCase(emailAddress)) {
            throw new DomainException("El correo ya está registrado.");
        }
        if (users.existsByIdentityDocument(doc)) {
            throw new DomainException("El documento ya está registrado.");
        }

        Role role = roles.findByName("PACIENTE")
                .orElseThrow(() -> new DomainException("El rol PACIENTE no está configurado."));

        User u = new User(name, emailAddress, doc, role, encoder.encode(password));
        users.save(u);

        Patient p = new Patient();
        p.setUser(u);
        p.setMedicalHistory(new MedicalHistory());
        p.setPhone(phone);
        p.setBirthDate(birthDate);
        patients.save(p);

        return createPair(u);
    }

    /**
     * Autentica un usuario y genera un nuevo par de tokens.
     * 
     * @param emailAddress Correo del usuario.
     * @param password Contraseña en texto plano.
     * @return Par de tokens (access + refresh).
     * @throws DomainException si las credenciales son inválidas o el usuario está inactivo.
     */
    @Transactional
    public TokenPair login(String emailAddress, String password) {
        User u = users.findByEmailIgnoreCase(emailAddress)
                .orElseThrow(() -> new DomainException("Credenciales inválidas."));

        if (!u.isActive() || !encoder.matches(password, u.getPasswordHash())) {
            throw new DomainException("Credenciales inválidas.");
        }

        u.setLastAccess(OffsetDateTime.now(ZoneOffset.UTC));
        users.save(u);
        return createPair(u);
    }

    /**
     * Rota el refresh token (revoca el anterior y genera uno nuevo).
     * 
     * @param raw Refresh token en texto plano.
     * @return Nuevo par de tokens (access + refresh).
     * @throws DomainException si el token es inválido, expirado o revocado.
     */
    @Transactional
    public TokenPair refresh(String raw) {
        RefreshToken old = refreshTokens.findByTokenHash(hash(raw))
                .orElseThrow(() -> new DomainException("Refresh token inválido."));

        if (old.isRevoked() || old.getExpiresAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            throw new DomainException("Refresh token expirado o revocado.");
        }

        old.setRevoked(true);
        refreshTokens.save(old);
        return createPair(old.getUser());
    }

    /**
     * Solicita recuperación de contraseña (envía token simulado).
     * 
     * @param emailAddress Correo del usuario (no se revela si existe para evitar enumeración).
     */
    @Transactional
    public void requestPasswordRecovery(String emailAddress) {
        User u = users.findByEmailIgnoreCase(emailAddress).orElse(null);
        if (u == null) return;

        // Revocar tokens activos anteriores del usuario
        resetTokens.revokeActiveTokens(u.getId());

        // Generar token simulado (en producción se enviaría por email)
        String raw = UUID.randomUUID() + "-" + UUID.randomUUID();

        PasswordResetToken t = new PasswordResetToken();
        t.setUser(u);
        t.setTokenHash(hash(raw));
        t.setExpiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(30));
        t.setUsed(false);
        resetTokens.save(t);

        email.send(u.getEmail(), "Recuperación de contraseña",
                "Token de recuperación simulado: " + raw);
    }

    /**
     * Restablece la contraseña usando un token de recuperación.
     * 
     * @param raw Token de recuperación en texto plano.
     * @param newPassword Nueva contraseña (se hashea).
     * @throws DomainException si el token es inválido, expirado o ya usado.
     */
    @Transactional
    public void resetPassword(String raw, String newPassword) {
        PasswordResetToken t = resetTokens.findByTokenHash(hash(raw))
                .orElseThrow(() -> new DomainException("Token de recuperación inválido."));

        if (t.isUsed() || t.getExpiresAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            throw new DomainException("El token expiró o ya fue utilizado.");
        }

        User u = t.getUser();
        u.setPasswordHash(encoder.encode(newPassword));
        users.save(u);

        t.setUsed(true);
        resetTokens.save(t);

        // Revocar todas las sesiones activas del usuario (refresh tokens)
        refreshTokens.revokeAllByUserId(u.getId());
    }

    /**
     * Crea un par de tokens (access + refresh) para un usuario.
     * 
     * @param u Usuario autenticado.
     * @return Par de tokens (el refresh se guarda en BD).
     */
    private TokenPair createPair(User u) {
        String access = jwt.generateAccessToken(u.getEmail(), u.getRole().getName());
        String raw = UUID.randomUUID() + "." + UUID.randomUUID();

        RefreshToken t = new RefreshToken();
        t.setUser(u);
        t.setTokenHash(hash(raw));
        t.setExpiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusDays(7));
        t.setRevoked(false);
        refreshTokens.save(t);

        Long patientId = null;
        Long professionalId = null;
        String role = u.getRole().getName();

        if ("PACIENTE".equals(role)) {
            Patient p = patients.findByUser(u).orElse(null);
            if (p != null) {
                patientId = p.getId();
            } else {
                System.out.println("⚠️ No se encontró paciente para user ID: " + u.getId());
            }
        } else if ("PROFESIONAL".equals(role)) {
            Professional p = professionals.findByUser(u).orElse(null);
            if (p != null) {
                professionalId = p.getId();
            } else {
                System.out.println("⚠️ No se encontró profesional para user ID: " + u.getId());
            }
        }

        return new TokenPair(access, raw, u.getId(), professionalId, patientId, role);
    }

    /**
     * Genera un hash SHA-256 de un texto plano (para tokens).
     * 
     * @param raw Texto a hashear.
     * @return Hash en Base64 URL safe sin padding.
     */
    private String hash(String raw) {
        try {
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(MessageDigest.getInstance("SHA-256")
                            .digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("No fue posible generar SHA-256", e);
        }
    }

    public record TokenPair(String accessToken, String refreshToken, Long userId, Long professionalId, Long patientId, String role) {}
}