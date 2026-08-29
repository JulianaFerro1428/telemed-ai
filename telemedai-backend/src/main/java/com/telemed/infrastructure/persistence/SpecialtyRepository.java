package com.telemed.infrastructure.persistence;

import com.telemed.domain.professional.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para la entidad Specialty (especialidades médicas).
 * 
 * Almacena las especialidades catalogadas (ej. Cardiología, Dermatología).
 * Se usa para asignar especialidades a los profesionales.
 */
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    // Métodos heredados: save(), findById(), findAll(), delete(), etc.
}