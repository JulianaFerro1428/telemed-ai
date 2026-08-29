package com.telemed.application.professional;

import com.telemed.domain.auth.User;
import com.telemed.domain.professional.Professional;
import com.telemed.infrastructure.persistence.*;
import com.telemed.shared.exception.DomainException;
import com.telemed.shared.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Servicio para gestionar profesionales de la salud (solo administradores).
 * 
 * Casos de uso:
 * - Registrar un nuevo profesional (requiere rol ADMIN).
 * - Listar todos los profesionales.
 * - Obtener un profesional por ID.
 */
@Service
public class ProfessionalService {
    private final ProfessionalRepository professionals;
    private final UserRepository users;
    private final RoleRepository roles;
    private final SpecialtyRepository specialties;
    private final PasswordEncoder encoder;

    public ProfessionalService(ProfessionalRepository p, UserRepository u,
                               RoleRepository r, SpecialtyRepository s,
                               PasswordEncoder e) {
        professionals = p;
        users = u;
        roles = r;
        specialties = s;
        encoder = e;
    }

    /**
     * Registra un nuevo profesional (versión del endpoint original).
     */
    @Transactional
    public Professional create(String fullName, String email, String document, String password,
                               String license, Long specialtyId, int years) {
        if (users.existsByEmailIgnoreCase(email)) {
            throw new DomainException("El correo ya está registrado.");
        }

        var role = roles.findByName("PROFESIONAL")
                .orElseThrow(() -> new DomainException("Rol PROFESIONAL no configurado."));
        var specialty = specialties.findById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada."));

        User u = new User(fullName, email, document, role, encoder.encode(password));
        u.setVerified(true);
        users.save(u);

        Professional p = new Professional();
        p.setUser(u);
        p.setLicenseNumber(license);
        p.setSpecialty(specialty);
        p.setYearsExperience(years);

        return professionals.save(p);
    }

    /**
     * Registra un profesional (versión mejorada con manejo de ID).
     */
    @Transactional
    public Professional createProfessional(String fullName, String email, String document, String password,
                                           String license, Long specialtyId, int years) {
        if (users.existsByEmailIgnoreCase(email)) {
            throw new DomainException("El correo ya está registrado.");
        }

        var role = roles.findByName("PROFESIONAL")
                .orElseThrow(() -> new DomainException("Rol PROFESIONAL no encontrado."));
        var specialty = specialties.findById(specialtyId)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidad no encontrada."));

        User user = new User(fullName, email, document, role, encoder.encode(password));
        user.setVerified(true);
        User savedUser = users.save(user);

        Professional professional = new Professional();
        professional.setUser(savedUser);
        professional.setLicenseNumber(license);
        professional.setSpecialty(specialty);
        professional.setYearsExperience(years);

        return professionals.save(professional);
    }

    public List<Professional> list() {
        return professionals.findAll();
    }

    /**
     * Obtiene un profesional por su ID.
     * Primero intenta por el ID de la tabla professionals.
     * Si no lo encuentra, intenta por el user_id (para compatibilidad con frontend).
     *
     * @param id ID del profesional o user_id.
     * @return Profesional encontrado.
     * @throws ResourceNotFoundException si no se encuentra.
     */
    @Transactional(readOnly = true)
    public Professional get(Long id) {
        // Intentar por ID de la tabla professionals
        return professionals.findById(id)
                .orElseGet(() -> {
                    // Si no, intentar por user_id
                    return professionals.findByUserId(id)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                "Profesional no encontrado para id: " + id
                            ));
                });
    }
}