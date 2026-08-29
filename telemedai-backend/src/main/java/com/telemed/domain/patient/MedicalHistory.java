package com.telemed.domain.patient;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de valor que encapsula los antecedentes médicos del paciente.
 * 
 * Es un Value Object (sin identidad propia) incrustado en la entidad Patient.
 * Almacena información relevante como alergias, enfermedades, cirugías, etc.
 * 
 * Se almacena como un campo de texto en la base de datos (TEXT).
 * Actualmente es un campo libre, pero puede estructurarse en el futuro.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistory {
    private String medical_history = "";
}