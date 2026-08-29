package com.telemed.interfaces.professional;

import com.telemed.application.professional.ProfessionalService;
import com.telemed.domain.professional.Professional;
import com.telemed.shared.dto.ProfessionalRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar profesionales de la salud.
 * 
 * Permite:
 * - Listar todos los profesionales (pacientes, profesionales, admin).
 * - Obtener un profesional por ID (pacientes, profesionales, admin).
 * - Registrar un profesional (solo administradores).
 * - Registrar un profesional usando el endpoint admin (solo administradores).
 * 
 * Los administradores tienen control total sobre los profesionales.
 */
@RestController
@RequestMapping("/api/professionals")
public class ProfessionalController {
    private final ProfessionalService service;

    public ProfessionalController(ProfessionalService service) {
        this.service = service;
    }

    /**
     * DTO para registrar un profesional (sin el método admin).
     */
    public record Request(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String identityDocument,
        @Size(min = 8) String password,
        @NotBlank String licenseNumber,
        @NotNull Long specialtyId,
        @Min(0) int yearsExperience
    ) {}

    /**
     * Lista todos los profesionales registrados.
     * 
     * @return Lista de profesionales.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('PACIENTE','PROFESIONAL','ADMIN')")
    @Operation(summary = "Listar profesionales")
    public Object list() {
        return service.list();
    }

    /**
     * Obtiene un profesional por su ID.
     * 
     * @param id ID del profesional.
     * @return Datos del profesional.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE','PROFESIONAL','ADMIN')")
    public Object get(@PathVariable Long id) {
        return service.get(id);
    }

    /**
     * Registra un nuevo profesional (solo administradores).
     * 
     * @param r Datos del profesional.
     * @return El profesional creado.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Registrar profesional")
    public Object create(@Valid @RequestBody Request r) {
        return service.create(
            r.fullName(),
            r.email(),
            r.identityDocument(),
            r.password(),
            r.licenseNumber(),
            r.specialtyId(),
            r.yearsExperience()
        );
    }

    /**
     * Registra un nuevo profesional usando el endpoint admin.
     * Similar al anterior pero con un DTO específico.
     * 
     * @param request Datos del profesional.
     * @return El profesional creado.
     */
    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Registrar profesional (solo admin)")
    public Professional registerProfessional(@Valid @RequestBody ProfessionalRequest request) {
        return service.createProfessional(
            request.fullName(),
            request.email(),
            request.identityDocument(),
            request.password(),
            request.licenseNumber(),
            request.specialtyId(),
            request.yearsExperience()
        );
    }
}