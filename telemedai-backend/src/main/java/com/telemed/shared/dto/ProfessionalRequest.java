package com.telemed.shared.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para registrar un profesional de la salud.
 * 
 * Este DTO se usa en el endpoint POST /api/professionals/admin/register.
 * Contiene todas las validaciones necesarias para garantizar que los datos sean correctos.
 * 
 * @param fullName Nombre completo del profesional (obligatorio).
 * @param email Correo electrónico (obligatorio, formato válido).
 * @param identityDocument Número de identificación (obligatorio).
 * @param password Contraseña (mínimo 8 caracteres).
 * @param licenseNumber Número de licencia profesional (obligatorio, único).
 * @param specialtyId ID de la especialidad (obligatorio, debe existir en la BD).
 * @param yearsExperience Años de experiencia (mínimo 0).
 */
public record ProfessionalRequest(
    @NotBlank String fullName,
    @NotBlank @Email String email,
    @NotBlank String identityDocument,
    @Size(min = 8) String password,
    @NotBlank String licenseNumber,
    @NotNull Long specialtyId,
    @Min(0) int yearsExperience
) {}