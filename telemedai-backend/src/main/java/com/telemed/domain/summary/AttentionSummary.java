package com.telemed.domain.summary;

import jakarta.persistence.*;
import lombok.*;

/**
 * Resumen de la atención médica registrada por el profesional.
 * 
 * Contiene el diagnóstico, recomendaciones, medicamentos prescritos,
 * observaciones y posible remisión a otra especialidad.
 * 
 * Se crea cuando el profesional finaliza la consulta.
 */
@Entity
@Table(name = "attention_summaries")
@Getter
@Setter
@NoArgsConstructor
public class AttentionSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @Column(columnDefinition = "TEXT")
    private String medications;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(columnDefinition = "TEXT")
    private String referral;
}