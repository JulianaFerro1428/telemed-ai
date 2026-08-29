package com.telemed.domain.summary;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

/**
 * Resumen posterior que combina la preconsulta y la atención.
 * 
 * Es el documento final que el paciente puede consultar y descargar.
 * Combina la información recopilada por el agente con los datos clínicos del profesional.
 */
@Entity
@Table(name = "post_summaries")
@Getter
@Setter
@NoArgsConstructor
public class PostSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "preconsultation_summary_id")
    private PreconsultationSummary preconsultationSummary;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "attention_summary_id")
    private AttentionSummary attentionSummary;

    @Column(name = "generated_at", nullable = false)
    private OffsetDateTime generatedAt;
}