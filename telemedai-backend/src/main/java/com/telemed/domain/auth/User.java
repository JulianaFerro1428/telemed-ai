package com.telemed.domain.auth;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

/**
 * Agregado raíz de identidad y autenticación.
 * 
 * Representa a cualquier persona que usa la plataforma: pacientes, profesionales, administradores.
 * Contiene las credenciales y el estado de la cuenta.
 * 
 * Invariantes:
 * - El nombre y el correo son obligatorios.
 * - El correo y el documento de identidad deben ser únicos.
 * - La contraseña se almacena hasheada (nunca en texto plano).
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 160)
    private String fullName;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(name = "identity_document", nullable = false, unique = true, length = 50)
    private String identityDocument;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean verified;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "registration_date", nullable = false)
    private OffsetDateTime registrationDate;

    @Column(name = "last_access")
    private OffsetDateTime lastAccess;

    /**
     * Constructor para crear un nuevo usuario.
     * 
     * @param fullName Nombre completo (no puede ser nulo o vacío).
     * @param email Correo electrónico (se almacena en minúsculas).
     * @param identityDocument Número de documento (único).
     * @param role Rol asignado.
     * @param passwordHash Hash de la contraseña (ya codificado con bcrypt).
     * @throws IllegalArgumentException si fullName o email son nulos/vacíos.
     */
    public User(String fullName, String email, String identityDocument,
                Role role, String passwordHash) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El correo es obligatorio.");
        }
        this.fullName = fullName;
        this.email = email.toLowerCase().trim();
        this.identityDocument = identityDocument;
        this.role = role;
        this.passwordHash = passwordHash;
        this.verified = false;
        this.active = true;
        this.registrationDate = OffsetDateTime.now();
    }
}