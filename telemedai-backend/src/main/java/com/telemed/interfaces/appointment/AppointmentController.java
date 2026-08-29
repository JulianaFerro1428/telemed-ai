package com.telemed.interfaces.appointment;

import com.telemed.application.appointment.AppointmentService;
import com.telemed.domain.appointment.AppointmentStatus;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.OffsetDateTime;

/**
 * Controlador REST para la gestión de citas médicas.
 * 
 * Expone endpoints para:
 * - Crear una cita (paciente o admin)
 * - Consultar una cita por ID
 * - Listar citas de un paciente
 * - Listar citas de un profesional
 * - Cancelar una cita
 * - Reprogramar una cita
 * - Cambiar el estado de una cita (profesional o admin)
 */
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    // ----- DTOs de entrada (records) -----
    public record CreateRequest(
        @NotNull Long patientId,
        @NotNull Long professionalId,
        @NotNull OffsetDateTime start,
        @NotNull OffsetDateTime end
    ) {}

    public record RescheduleRequest(
        @NotNull OffsetDateTime start,
        @NotNull OffsetDateTime end
    ) {}

    public record CancelRequest(
        @NotBlank String reason
    ) {}

    public record StatusRequest(
        @NotNull AppointmentStatus status
    ) {}

    // ----- Endpoints -----

    /**
     * Crea una nueva cita.
     * Solo pacientes o administradores pueden agendar.
     * 
     * @param r Datos de la cita (paciente, profesional, horario).
     * @return La cita recién creada.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('PACIENTE','ADMIN')")
    @Operation(summary = "Agendar cita")
    public Object create(@Valid @RequestBody CreateRequest r) {
        return service.create(r.patientId(), r.professionalId(), r.start(), r.end());
    }

    /**
     * Obtiene los detalles de una cita por su ID.
     * Cualquier usuario autenticado puede consultar.
     * 
     * @param id ID de la cita.
     * @return Datos de la cita.
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Object get(@PathVariable Long id) {
        return service.get(id);
    }

    /**
     * Lista todas las citas de un paciente.
     * Solo el propio paciente o un administrador pueden acceder.
     * 
     * @param id ID del paciente.
     * @return Lista de citas del paciente.
     */
    @GetMapping("/patient/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE','ADMIN')")
    public Object patient(@PathVariable Long id) {
        return service.byPatient(id);
    }

    /**
     * Lista todas las citas de un profesional.
     * Solo el propio profesional o un administrador pueden acceder.
     * 
     * @param id ID del profesional.
     * @return Lista de citas del profesional.
     */
    @GetMapping("/professional/{id}")
    @PreAuthorize("hasAnyRole('PROFESIONAL','ADMIN')")
    public Object professional(@PathVariable Long id) {
        return service.byProfessional(id);
    }

    /**
     * Cancela una cita.
     * Solo el paciente o un administrador pueden cancelar.
     * 
     * @param id ID de la cita a cancelar.
     * @param r Motivo de la cancelación.
     * @return La cita actualizada (estado CANCELADA).
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('PACIENTE','ADMIN')")
    public Object cancel(@PathVariable Long id, @Valid @RequestBody CancelRequest r) {
        return service.cancel(id, r.reason());
    }

    /**
     * Reprograma una cita a un nuevo horario.
     * Solo el paciente o un administrador pueden reprogramar.
     * 
     * @param id ID de la cita a reprogramar.
     * @param r Nuevo horario (start y end).
     * @return La cita actualizada (estado REPROGRAMADA).
     */
    @PatchMapping("/{id}/reschedule")
    @PreAuthorize("hasAnyRole('PACIENTE','ADMIN')")
    public Object reschedule(@PathVariable Long id, @Valid @RequestBody RescheduleRequest r) {
        return service.reschedule(id, r.start(), r.end());
    }

    /**
     * Cambia el estado de una cita (ej. de CONFIRMADA a COMPLETADA).
     * Solo el profesional asignado o un administrador pueden modificar el estado.
     * 
     * @param id ID de la cita.
     * @param r Nuevo estado.
     * @return La cita actualizada.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('PROFESIONAL','ADMIN')")
    public Object status(@PathVariable Long id, @Valid @RequestBody StatusRequest r) {
        return service.updateStatus(id, r.status());
    }
}