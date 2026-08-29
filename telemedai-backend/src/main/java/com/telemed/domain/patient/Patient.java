package com.telemed.domain.patient;

import com.telemed.domain.auth.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Perfil especializado del usuario con rol de paciente.
 * 
 * Extiende la información de User con datos específicos del paciente:
 * - Fecha de nacimiento
 * - Teléfono
 * - Historial médico (Value Object)
 * 
 * Se relaciona con User mediante @MapsId (comparte la misma clave primaria).
 * La carga de User es EAGER para evitar problemas de LazyInitialization.
 */
@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
public class Patient {
    /**
     * ID del paciente, que es el mismo que el ID del User asociado.
     */
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(length = 30)
    private String phone;

    @Embedded
    private MedicalHistory medicalHistory = new MedicalHistory();
}