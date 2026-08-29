package com.telemed.domain.professional;

import jakarta.persistence.*;
import lombok.*;

/**
 * Especialidad médica catalogada.
 * 
 * Representa una rama de la medicina (Cardiología, Dermatología, etc.).
 * Es una entidad de referencia que se usa para asignar especialidades a los profesionales.
 */
@Entity
@Table(name = "specialties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;
}