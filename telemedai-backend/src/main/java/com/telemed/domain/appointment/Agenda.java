package com.telemed.domain.appointment;

import com.telemed.domain.professional.Professional;
import jakarta.persistence.*;
import lombok.*;

/**
 * Agenda básica del profesional.
 * 
 * Representa el conjunto de horarios que un profesional tiene disponibles.
 * Actualmente es una entidad simple que se puede extender en el futuro
 * para incluir bloques de disponibilidad (horarios específicos).
 * 
 * Se relaciona con Professional mediante una relación OneToOne.
 */
@Entity
@Table(name = "agendas")
@Getter
@Setter
@NoArgsConstructor
public class Agenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Profesional al que pertenece esta agenda.
     * Relación OneToOne con carga LAZY para evitar sobrecarga innecesaria.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professional_id", nullable = false)
    private Professional professional;
}