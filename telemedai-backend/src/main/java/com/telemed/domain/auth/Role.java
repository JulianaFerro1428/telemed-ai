package com.telemed.domain.auth;

import jakarta.persistence.*;
import lombok.*;

/**
 * Rol de usuario en el sistema.
 * 
 * Define los permisos y funcionalidades accesibles.
 * Roles predefinidos: PACIENTE, PROFESIONAL, ADMIN.
 * 
 * Spring Security usa el nombre del rol para autorización (@PreAuthorize).
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre único del rol (ej. "PACIENTE", "PROFESIONAL", "ADMIN").
     */
    @Column(nullable = false, unique = true, length = 30)
    private String name;
}