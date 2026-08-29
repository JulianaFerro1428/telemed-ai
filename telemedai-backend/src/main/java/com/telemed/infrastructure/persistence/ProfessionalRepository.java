package com.telemed.infrastructure.persistence;

import com.telemed.domain.auth.User;
import com.telemed.domain.professional.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Professional (profesionales).
 * 
 * Maneja los perfiles de los profesionales de la salud.
 */
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
    /**
     * Lista todos los profesionales que pertenecen a una especialidad específica.
     * 
     * @param specialtyId ID de la especialidad.
     * @return Lista de profesionales en esa especialidad.
     */
    List<Professional> findBySpecialtyId(Long specialtyId);
    Optional<Professional> findByUser(User user);
    Optional<Professional> findByUserId(Long userId);
}