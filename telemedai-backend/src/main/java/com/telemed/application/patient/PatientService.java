package com.telemed.application.patient;

import com.telemed.domain.patient.Patient;
import com.telemed.infrastructure.persistence.PatientRepository;
import com.telemed.infrastructure.persistence.UserRepository;
import com.telemed.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

/**
 * Servicio para gestionar el perfil del paciente.
 * 
 * Implementa casos de uso:
 * - Consultar datos del paciente.
 * - Actualizar datos personales (nombre, teléfono, fecha de nacimiento, historial médico).
 */
@Service
public class PatientService {
    private final PatientRepository patients;
    private final UserRepository users;

    public PatientService(PatientRepository patients, UserRepository users) {
        this.patients = patients;
        this.users = users;
    }

    /**
     * Obtiene un paciente por su ID.
     * 
     * @param id ID del paciente.
     * @return Paciente encontrado.
     * @throws ResourceNotFoundException si no existe.
     */
    @Transactional(readOnly = true)
    public Patient get(Long id) {
        return patients.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado."));
    }

    /**
     * Actualiza los datos del paciente.
     * 
     * @param id ID del paciente.
     * @param fullName Nuevo nombre completo.
     * @param phone Nuevo teléfono.
     * @param birthDate Nueva fecha de nacimiento.
     * @param history Nuevo historial médico (se guarda como texto).
     * @return Paciente actualizado.
     */
    @Transactional
    public Patient update(Long id, String fullName, String phone,
                          LocalDate birthDate, String history) {
        Patient p = get(id);

        p.getUser().setFullName(fullName);
        p.setPhone(phone);
        p.setBirthDate(birthDate);
        p.getMedicalHistory().setMedical_history(history == null ? "" : history);

        users.save(p.getUser());
        return patients.save(p);
    }
}