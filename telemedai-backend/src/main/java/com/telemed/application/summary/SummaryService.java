package com.telemed.application.summary;

import com.telemed.domain.summary.*;
import com.telemed.infrastructure.persistence.*;
import com.telemed.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;

/**
 * Servicio para gestionar los resúmenes clínicos.
 * 
 * Permite crear y consultar:
 * - Resumen de preconsulta (información inicial del paciente).
 * - Resumen de atención (diagnóstico, tratamiento, medicamentos).
 * - Resumen posterior (combina ambos).
 * 
 * Todos los resúmenes se asocian a una cita.
 */
@Service
public class SummaryService {
    private final PreconsultationSummaryRepository pre;
    private final AttentionSummaryRepository attention;
    private final PostSummaryRepository post;
    private final AppointmentRepository appointments;

    public SummaryService(PreconsultationSummaryRepository p,
                          AttentionSummaryRepository a,
                          PostSummaryRepository ps,
                          AppointmentRepository ap) {
        pre = p;
        attention = a;
        post = ps;
        appointments = ap;
    }

    /**
     * Crea un resumen de preconsulta y lo asocia a una cita.
     * 
     * @param appointmentId ID de la cita.
     * @param reason Motivo de consulta.
     * @param evolution Tiempo de evolución.
     * @param symptoms Síntomas detallados.
     * @param history Antecedentes relevantes.
     * @return Resumen de preconsulta creado.
     */
    @Transactional
    public PreconsultationSummary createPre(Long appointmentId, String reason,
                                            String evolution, String symptoms,
                                            String history) {
        var ap = appointments.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada."));

        var s = new PreconsultationSummary();
        s.setConsultationReason(reason);
        s.setEvolutionTime(evolution);
        s.setDetailedSymptoms(symptoms);
        s.setRelevantHistory(history);
        s = pre.save(s);

        ap.setPreconsultationSummaryId(s.getId());
        appointments.save(ap);

        return s;
    }

    /**
     * Crea un resumen de atención (diagnóstico, medicamentos, etc.).
     * 
     * @param appointmentId ID de la cita.
     * @param diagnosis Diagnóstico.
     * @param recommendations Recomendaciones.
     * @param medications Medicamentos prescritos (texto).
     * @param observations Observaciones adicionales.
     * @param referral Remisión (opcional).
     * @return Resumen de atención creado.
     */
    @Transactional
    public AttentionSummary createAttention(Long appointmentId, String diagnosis,
                                            String recommendations, String medications,
                                            String observations, String referral) {
        appointments.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada."));

        var s = new AttentionSummary();
        s.setDiagnosis(diagnosis);
        s.setRecommendations(recommendations);
        s.setMedications(medications);
        s.setObservations(observations);
        s.setReferral(referral);

        return attention.save(s);
    }

    /**
     * Crea un resumen posterior combinando preconsulta y atención.
     * 
     * @param appointmentId ID de la cita.
     * @param preId ID del resumen de preconsulta.
     * @param attentionId ID del resumen de atención.
     * @return Resumen posterior creado.
     */
    @Transactional
    public PostSummary createPost(Long appointmentId, Long preId, Long attentionId) {
        var ap = appointments.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada."));

        var p = pre.findById(preId)
                .orElseThrow(() -> new ResourceNotFoundException("Resumen de preconsulta no encontrado."));
        var a = attention.findById(attentionId)
                .orElseThrow(() -> new ResourceNotFoundException("Resumen de atención no encontrado."));

        var s = new PostSummary();
        s.setPreconsultationSummary(p);
        s.setAttentionSummary(a);
        s.setGeneratedAt(OffsetDateTime.now(ZoneOffset.UTC));
        s = post.save(s);

        ap.setPreconsultationSummaryId(p.getId());
        ap.setPostSummaryId(s.getId());
        appointments.save(ap);

        return s;
    }

    public PreconsultationSummary getPre(Long id) {
        return pre.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resumen de preconsulta no encontrado."));
    }

    public AttentionSummary getAttention(Long id) {
        return attention.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resumen de atención no encontrado."));
    }

    public PostSummary getPost(Long id) {
        return post.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resumen posterior no encontrado."));
    }
}