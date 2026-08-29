package com.telemed.domain.appointment;

import com.telemed.domain.patient.Patient;
import com.telemed.domain.professional.Professional;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

/**
 * Agregado raíz que representa una cita médica.
 * 
 * Contiene la información esencial de la cita: paciente, profesional,
 * horario, estado y referencias a los resúmenes asociados.
 * 
 * Invariantes y reglas de negocio:
 * - La fecha de fin debe ser posterior a la de inicio (validado en constructor y métodos).
 * - El estado solo puede cambiar según la máquina de estados definida.
 * - No se puede cancelar/reprogramar/completar una cita en estado terminal.
 */
@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Paciente que solicita la cita.
     * Relación EAGER para evitar LazyInitializationException al serializar.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /**
     * Profesional que atenderá la cita.
     * Relación EAGER por la misma razón.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "professional_id", nullable = false)
    private Professional professional;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    /**
     * Estado actual de la cita (ver AppointmentStatus).
     * Almacenado como string en la BD (EnumType.STRING) para legibilidad.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AppointmentStatus status;

    /**
     * ID del resumen de preconsulta asociado (si existe).
     * Se usa como referencia a la tabla de preconsultation_summaries.
     */
    @Column(name = "preconsultation_summary_id")
    private Long preconsultationSummaryId;

    /**
     * ID del resumen posterior (post) asociado.
     */
    @Column(name = "post_summary_id")
    private Long postSummaryId;

    /**
     * Motivo de cancelación (solo si la cita se cancela).
     */
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    /**
     * Constructor que crea una cita CONFIRMADA.
     * 
     * @param patient Paciente que agenda.
     * @param professional Profesional seleccionado.
     * @param start Fecha y hora de inicio.
     * @param end Fecha y hora de fin.
     * @throws IllegalArgumentException si end <= start.
     */
    public Appointment(Patient patient, Professional professional,
                       OffsetDateTime start, OffsetDateTime end) {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("El fin debe ser posterior al inicio.");
        }
        this.patient = patient;
        this.professional = professional;
        this.startTime = start;
        this.endTime = end;
        this.status = AppointmentStatus.CONFIRMADA;
    }

    /**
     * Cancela la cita.
     * Solo permite cancelar si está CONFIRMADA o REPROGRAMADA.
     * 
     * @param reason Motivo de la cancelación.
     * @throws IllegalStateException si la cita está en estado terminal.
     */
    public void cancel(String reason) {
        if (this.status != AppointmentStatus.CONFIRMADA &&
            this.status != AppointmentStatus.REPROGRAMADA) {
            throw new IllegalStateException(
                "No se puede cancelar una cita en estado: " + this.status
            );
        }
        this.status = AppointmentStatus.CANCELADA;
        this.cancellationReason = reason;
    }

    /**
     * Reprograma la cita a un nuevo horario.
     * Solo permite reprogramar si está CONFIRMADA o REPROGRAMADA.
     * 
     * @param start Nueva fecha de inicio.
     * @param end Nueva fecha de fin.
     * @throws IllegalArgumentException si end <= start.
     * @throws IllegalStateException si la cita está en estado terminal.
     */
    public void reschedule(OffsetDateTime start, OffsetDateTime end) {
        if (this.status != AppointmentStatus.CONFIRMADA &&
            this.status != AppointmentStatus.REPROGRAMADA) {
            throw new IllegalStateException(
                "No se puede reprogramar una cita en estado: " + this.status
            );
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("El fin debe ser posterior al inicio.");
        }
        this.startTime = start;
        this.endTime = end;
        this.status = AppointmentStatus.REPROGRAMADA;
    }

    /**
     * Marca la cita como COMPLETADA.
     * Solo permite si está CONFIRMADA o REPROGRAMADA.
     * 
     * @throws IllegalStateException si la cita está en estado terminal.
     */
    public void markAsCompleted() {
        if (this.status != AppointmentStatus.CONFIRMADA &&
            this.status != AppointmentStatus.REPROGRAMADA) {
            throw new IllegalStateException(
                "No se puede completar una cita en estado: " + this.status
            );
        }
        this.status = AppointmentStatus.COMPLETADA;
    }

    /**
     * Confirma una cita que estaba REPROGRAMADA (la vuelve a CONFIRMADA).
     * 
     * @throws IllegalStateException si la cita no está REPROGRAMADA.
     */
    public void confirm() {
        if (this.status != AppointmentStatus.REPROGRAMADA) {
            throw new IllegalStateException(
                "Solo se puede confirmar una cita reprogramada."
            );
        }
        this.status = AppointmentStatus.CONFIRMADA;
    }
}