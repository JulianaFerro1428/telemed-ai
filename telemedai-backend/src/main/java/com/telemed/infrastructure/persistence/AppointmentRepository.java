package com.telemed.infrastructure.persistence;

import com.telemed.domain.appointment.Appointment;
import com.telemed.domain.appointment.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repositorio JPA para la entidad Appointment (citas médicas).
 * 
 * Proporciona métodos de acceso a la base de datos específicos para citas.
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    /**
     * Lista todas las citas de un paciente, ordenadas por fecha descendente.
     * 
     * @param patientId ID del paciente.
     * @return Lista de citas del paciente.
     */
    List<Appointment> findByPatientIdOrderByStartTimeDesc(Long patientId);

    /**
     * Lista todas las citas de un profesional, ordenadas por fecha ascendente.
     * 
     * @param professionalId ID del profesional.
     * @return Lista de citas del profesional.
     */
    List<Appointment> findByProfessionalIdOrderByStartTimeAsc(Long professionalId);

    /**
     * Verifica si existe alguna cita de un profesional en un rango de tiempo específico.
     * 
     * Solo considera citas en estado CONFIRMADA o REPROGRAMADA (activas).
     * Este método se usa para validar disponibilidad al crear o reprogramar citas.
     * 
     * @param professionalId ID del profesional.
     * @param statuses Lista de estados a considerar (ej. [CONFIRMADA, REPROGRAMADA]).
     * @param end Fecha de fin del rango (end > start).
     * @param start Fecha de inicio del rango.
     * @return true si existe alguna cita que se solape con el rango.
     */
    boolean existsByProfessionalIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
        Long professionalId,
        List<AppointmentStatus> statuses,
        OffsetDateTime end,
        OffsetDateTime start
    );
}