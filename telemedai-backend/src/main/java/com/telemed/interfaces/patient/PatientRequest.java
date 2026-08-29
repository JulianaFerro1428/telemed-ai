package com.telemed.interfaces.patient;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * DTO para actualizar el perfil del paciente.
 */
public record PatientRequest(
    @NotBlank String fullName,
    @Size(max = 30) String phone,
    LocalDate birthDate,
    @Size(max = 5000) String medicalHistory
) {}