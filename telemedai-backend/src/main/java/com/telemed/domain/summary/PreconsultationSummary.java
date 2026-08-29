package com.telemed.domain.summary;

import jakarta.persistence.*;
import lombok.*;

/**
 * Resumen estructurado de la preconsulta.
 * 
 * Contiene la información recopilada por el agente inteligente:
 * - Motivo de consulta
 * - Tiempo de evolución
 * - Síntomas detallados
 * - Antecedentes relevantes
 * 
 * El agente NO diagnostica ni prescribe; solo organiza la información.
 */
@Entity
@Table(name = "preconsultation_summaries")
@Getter
@Setter
@NoArgsConstructor
public class PreconsultationSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consultation_reason", columnDefinition = "TEXT")
    private String consultationReason;

    @Column(name = "evolution_time", length = 200)
    private String evolutionTime;

    @Column(name = "detailed_symptoms", columnDefinition = "TEXT")
    private String detailedSymptoms;

    @Column(name = "relevant_history", columnDefinition = "TEXT")
    private String relevantHistory;
}