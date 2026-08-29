package com.telemed.infrastructure.persistence;

import com.telemed.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad User (usuarios).
 * 
 * Es el repositorio más importante, ya que User es la raíz de identidad.
 * Proporciona métodos para buscar usuarios por email y verificar unicidad.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Busca un usuario por su correo electrónico (case-insensitive).
     * 
     * @param email Correo electrónico del usuario.
     * @return Optional con el usuario si existe.
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Verifica si ya existe un usuario con ese correo electrónico.
     * 
     * @param email Correo electrónico a verificar.
     * @return true si el email ya está registrado.
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Verifica si ya existe un usuario con ese documento de identidad.
     * 
     * @param identityDocument Documento de identidad a verificar.
     * @return true si el documento ya está registrado.
     */
    boolean existsByIdentityDocument(String identityDocument);
}