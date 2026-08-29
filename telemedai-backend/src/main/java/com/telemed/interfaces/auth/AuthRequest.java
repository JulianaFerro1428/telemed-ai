package com.telemed.interfaces.auth;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
/**
 * DTO para la solicitud de registro de un nuevo paciente.
 */
public record AuthRequest(
    @NotBlank String fullName,
    @NotBlank @Email String email,
    @NotBlank String identityDocument,
    @Size(min = 8, max = 100) String password,
    String phone,
    LocalDate birthDate
) {}