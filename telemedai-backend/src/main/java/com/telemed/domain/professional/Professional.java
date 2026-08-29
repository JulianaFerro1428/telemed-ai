package com.telemed.domain.professional;

import com.telemed.domain.auth.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Perfil del profesional de la salud.
 * 
 * Extiende la información de User con datos específicos del profesional:
 * - Número de licencia (único)
 * - Especialidad
 * - Años de experiencia
 * 
 * Se relaciona con User mediante una columna user_id (no comparte PK).
 * La relación es OneToOne con carga EAGER para evitar LazyInitialization.
 */
@Entity
@Table(name = "professionals")
@Getter
@Setter
@NoArgsConstructor
public class Professional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "license_number", nullable = false, unique = true, length = 80)
    private String licenseNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @Column(name = "years_experience", nullable = false)
    private int yearsExperience;
}