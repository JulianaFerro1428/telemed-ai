package com.telemed.interfaces.summary;

import com.telemed.application.summary.SummaryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar resúmenes clínicos.
 * 
 * Permite:
 * - Crear resumen de preconsulta (paciente, profesional o admin).
 * - Consultar resumen de preconsulta (paciente, profesional o admin).
 * - Crear resumen de atención (profesional o admin).
 * - Consultar resumen de atención (profesional o admin).
 * - Crear resumen posterior (combinación) (profesional o admin).
 * - Consultar resumen posterior (paciente, profesional o admin).
 * 
 * Los resúmenes son parte fundamental del historial médico del paciente.
 */
@RestController
@RequestMapping("/api/summaries")
public class SummaryController {
    private final SummaryService service;

    public SummaryController(SummaryService service) {
        this.service = service;
    }

    // ----- DTOs de entrada -----
    public record PreReq(
        @NotNull Long appointmentId,
        @NotBlank String consultationReason,
        @Size(max = 200) String evolutionTime,
        @NotBlank String detailedSymptoms,
        String relevantHistory
    ) {}

    public record AttentionReq(
        @NotNull Long appointmentId,
        @NotBlank String diagnosis,
        @NotBlank String recommendations,
        String medications,
        String observations,
        String referral
    ) {}

    public record PostReq(
        @NotNull Long appointmentId,
        @NotNull Long preconsultationSummaryId,
        @NotNull Long attentionSummaryId
    ) {}

    // ----- Endpoints de preconsulta -----

    /**
     * Crea un resumen de preconsulta asociado a una cita.
     * 
     * @param r Datos de la preconsulta (motivo, evolución, síntomas, historial).
     * @return El resumen de preconsulta creado.
     */
    @PostMapping("/preconsultation")
    @PreAuthorize("hasAnyRole('PACIENTE','PROFESIONAL','ADMIN')")
    @Operation(summary = "Crear resumen de preconsulta")
    public Object pre(@Valid @RequestBody PreReq r) {
        return service.createPre(
            r.appointmentId(),
            r.consultationReason(),
            r.evolutionTime(),
            r.detailedSymptoms(),
            r.relevantHistory()
        );
    }

    /**
     * Obtiene un resumen de preconsulta por su ID.
     * 
     * @param id ID del resumen.
     * @return El resumen de preconsulta.
     */
    @GetMapping("/preconsultation/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE','PROFESIONAL','ADMIN')")
    public Object getPre(@PathVariable Long id) {
        return service.getPre(id);
    }

    // ----- Endpoints de atención -----

    /**
     * Crea un resumen de atención (registro médico) para una cita.
     * Solo profesionales o administradores pueden crear.
     * 
     * @param r Datos de la atención (diagnóstico, recomendaciones, medicamentos, etc.).
     * @return El resumen de atención creado.
     */
    @PostMapping("/attention")
    @PreAuthorize("hasAnyRole('PROFESIONAL','ADMIN')")
    @Operation(summary = "Registrar atención médica")
    public Object attention(@Valid @RequestBody AttentionReq r) {
        return service.createAttention(
            r.appointmentId(),
            r.diagnosis(),
            r.recommendations(),
            r.medications(),
            r.observations(),
            r.referral()
        );
    }

    /**
     * Obtiene un resumen de atención por su ID.
     * 
     * @param id ID del resumen.
     * @return El resumen de atención.
     */
    @GetMapping("/attention/{id}")
    @PreAuthorize("hasAnyRole('PROFESIONAL','ADMIN')")
    public Object getAttention(@PathVariable Long id) {
        return service.getAttention(id);
    }

    // ----- Endpoints de resumen posterior -----

    /**
     * Crea un resumen posterior combinando preconsulta y atención.
     * 
     * @param r IDs de la cita, preconsulta y atención.
     * @return El resumen posterior creado.
     */
    @PostMapping("/post")
    @PreAuthorize("hasAnyRole('PROFESIONAL','ADMIN')")
    @Operation(summary = "Generar resumen posterior")
    public Object post(@Valid @RequestBody PostReq r) {
        return service.createPost(
            r.appointmentId(),
            r.preconsultationSummaryId(),
            r.attentionSummaryId()
        );
    }

    /**
     * Obtiene un resumen posterior por su ID.
     * 
     * @param id ID del resumen.
     * @return El resumen posterior.
     */
    @GetMapping("/post/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE','PROFESIONAL','ADMIN')")
    public Object getPost(@PathVariable Long id) {
        return service.getPost(id);
    }
}