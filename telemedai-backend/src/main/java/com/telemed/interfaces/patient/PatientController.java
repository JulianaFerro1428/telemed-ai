package com.telemed.interfaces.patient;

import com.telemed.application.patient.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar el perfil del paciente.
 * 
 * Permite:
 * - Consultar los datos de un paciente (paciente o admin).
 * - Actualizar los datos de un paciente (paciente o admin).
 * 
 * Los pacientes solo pueden acceder a su propio perfil,
 * mientras que los administradores pueden acceder a cualquier perfil.
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {
    private final PatientService service;

    public PatientController(PatientService service) {
        this.service = service;
    }

    /**
     * Consulta los datos de un paciente por su ID.
     * 
     * @param id ID del paciente.
     * @return Datos del paciente (incluyendo User y MedicalHistory).
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE','ADMIN')")
    @Operation(summary = "Consultar paciente")
    public Object get(@PathVariable Long id) {
        return service.get(id);
    }

    /**
     * Actualiza los datos de un paciente.
     * 
     * @param id ID del paciente.
     * @param r Datos a actualizar (nombre, teléfono, fecha de nacimiento, historial médico).
     * @return El paciente actualizado.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE','ADMIN')")
    @Operation(summary = "Actualizar paciente")
    public Object update(@PathVariable Long id, @Valid @RequestBody PatientRequest r) {
        return service.update(id, r.fullName(), r.phone(), r.birthDate(), r.medicalHistory());
    }
}