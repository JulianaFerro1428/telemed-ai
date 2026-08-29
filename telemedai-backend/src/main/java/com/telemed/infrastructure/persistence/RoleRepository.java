package com.telemed.infrastructure.persistence;

import com.telemed.domain.auth.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Role (roles de usuario).
 * 
 * Almacena los roles predefinidos: PACIENTE, PROFESIONAL, ADMIN.
 * Se usa para asignar roles a los usuarios durante el registro.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Busca un rol por su nombre (ej. "PACIENTE").
     * 
     * @param name Nombre del rol.
     * @return Optional con el rol si existe.
     */
    Optional<Role> findByName(String name);
}