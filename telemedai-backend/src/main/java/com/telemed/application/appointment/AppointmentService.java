package com.telemed.application.appointment;

import com.telemed.domain.appointment.*;
import com.telemed.infrastructure.email.EmailSender;
import com.telemed.infrastructure.persistence.*;
import com.telemed.shared.exception.DomainException;
import com.telemed.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Servicio que implementa los casos de uso para la gestión de citas.
 * 
 * Orquesta todas las operaciones del ciclo de vida de una cita:
 * - Creación (agendamiento)
 * - Cancelación
 * - Reprogramación
 * - Cambio de estado (máquina de estados)
 * - Consultas (por paciente, por profesional, por ID)
 */
@Service
public class AppointmentService {
    private final AppointmentRepository appointments;
    private final PatientRepository patients;
    private final ProfessionalRepository professionals;
    private final EmailSender email;

    public AppointmentService(AppointmentRepository a, PatientRepository p,
                              ProfessionalRepository pr, EmailSender email) {
        this.appointments = a;
        this.patients = p;
        this.professionals = pr;
        this.email = email;
    }

    /**
     * Crea una nueva cita confirmada.
     * 
     * @param patientId ID del paciente que agenda la cita.
     * @param professionalId ID del profesional seleccionado.
     * @param start Fecha y hora de inicio.
     * @param end Fecha y hora de fin.
     * @return La cita recién creada (estado CONFIRMADA).
     * @throws ResourceNotFoundException si paciente o profesional no existen.
     * @throws DomainException si el profesional no está disponible en ese horario.
     */
    @Transactional
    public Appointment create(Long patientId, Long professionalId,
                              OffsetDateTime start, OffsetDateTime end) {
        var patient = patients.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado."));
        var professional = professionals.findById(professionalId)
                .orElseThrow(() -> new ResourceNotFoundException("Profesional no encontrado."));

        // Verificar disponibilidad del profesional
        boolean overlap = appointments.existsByProfessionalIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                professionalId,
                List.of(AppointmentStatus.CONFIRMADA, AppointmentStatus.REPROGRAMADA),
                end,
                start
        );
        if (overlap) {
            throw new DomainException("El profesional no está disponible en ese horario.");
        }

        Appointment ap = new Appointment(patient, professional, start, end);
        ap = appointments.save(ap);
        email.send(patient.getUser().getEmail(), "Cita confirmada", "Su cita ha sido confirmada.");
        return ap;
    }

    /**
     * Cancela una cita (solo si está CONFIRMADA o REPROGRAMADA).
     * 
     * @param id ID de la cita a cancelar.
     * @param reason Motivo de la cancelación.
     * @return La cita actualizada con estado CANCELADA.
     * @throws ResourceNotFoundException si la cita no existe.
     * @throws DomainException si la cita está en un estado terminal.
     */
    @Transactional
    public Appointment cancel(Long id, String reason) {
        Appointment ap = get(id);

        // Regla de negocio: solo se puede cancelar si está CONFIRMADA o REPROGRAMADA
        if (ap.getStatus() != AppointmentStatus.CONFIRMADA &&
            ap.getStatus() != AppointmentStatus.REPROGRAMADA) {
            throw new DomainException("No se puede cancelar una cita en estado: " + ap.getStatus());
        }

        ap.setStatus(AppointmentStatus.CANCELADA);
        ap.setCancellationReason(reason);
        ap = appointments.save(ap);

        email.send(ap.getPatient().getUser().getEmail(), "Cita cancelada", "La cita fue cancelada.");
        return ap;
    }

    /**
     * Reprograma una cita a un nuevo horario (solo si está CONFIRMADA o REPROGRAMADA).
     * 
     * @param id ID de la cita a reprogramar.
     * @param start Nueva fecha de inicio.
     * @param end Nueva fecha de fin.
     * @return La cita actualizada con estado REPROGRAMADA.
     * @throws ResourceNotFoundException si la cita no existe.
     * @throws DomainException si la cita está en estado terminal o el nuevo horario no está disponible.
     */
    @Transactional
    public Appointment reschedule(Long id, OffsetDateTime start, OffsetDateTime end) {
        Appointment ap = get(id);

        // Regla de negocio: solo se puede reprogramar si está CONFIRMADA o REPROGRAMADA
        if (ap.getStatus() != AppointmentStatus.CONFIRMADA &&
            ap.getStatus() != AppointmentStatus.REPROGRAMADA) {
            throw new DomainException("No se puede reprogramar una cita en estado: " + ap.getStatus());
        }

        // Validar que la nueva fecha sea válida
        if (!end.isAfter(start)) {
            throw new DomainException("La fecha de fin debe ser posterior a la de inicio.");
        }

        // Verificar disponibilidad del profesional para el nuevo horario
        boolean overlap = appointments.existsByProfessionalIdAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                ap.getProfessional().getId(),
                List.of(AppointmentStatus.CONFIRMADA, AppointmentStatus.REPROGRAMADA),
                end,
                start
        );
        if (overlap) {
            throw new DomainException("El profesional no está disponible en ese nuevo horario.");
        }

        ap.setStartTime(start);
        ap.setEndTime(end);
        ap.setStatus(AppointmentStatus.REPROGRAMADA);
        ap = appointments.save(ap);

        email.send(ap.getPatient().getUser().getEmail(), "Cita reprogramada", "Su cita ha sido reprogramada.");
        return ap;
    }

    /**
     * Cambia el estado de una cita siguiendo la máquina de estados.
     * 
     * Transiciones permitidas:
     * - CONFIRMADA → REPROGRAMADA, CANCELADA, COMPLETADA, NO_ASISTIO
     * - REPROGRAMADA → CONFIRMADA, CANCELADA
     * - Estados terminales (COMPLETADA, CANCELADA, NO_ASISTIO) no se pueden modificar.
     * 
     * @param id ID de la cita.
     * @param newStatus Nuevo estado a asignar.
     * @return La cita actualizada.
     * @throws ResourceNotFoundException si la cita no existe.
     * @throws DomainException si la transición no está permitida.
     */
    @Transactional
    public Appointment updateStatus(Long id, AppointmentStatus newStatus) {
        Appointment ap = get(id);
        AppointmentStatus current = ap.getStatus();

        // Máquina de estados
        switch (current) {
            case CONFIRMADA:
                if (newStatus != AppointmentStatus.REPROGRAMADA &&
                    newStatus != AppointmentStatus.CANCELADA &&
                    newStatus != AppointmentStatus.COMPLETADA &&
                    newStatus != AppointmentStatus.NO_ASISTIO) {
                    throw new DomainException(
                        "Desde CONFIRMADA solo se puede pasar a REPROGRAMADA, CANCELADA, COMPLETADA o NO_ASISTIO."
                    );
                }
                break;

            case REPROGRAMADA:
                if (newStatus != AppointmentStatus.CONFIRMADA &&
                    newStatus != AppointmentStatus.CANCELADA) {
                    throw new DomainException(
                        "Desde REPROGRAMADA solo se puede pasar a CONFIRMADA o CANCELADA."
                    );
                }
                break;

            case COMPLETADA:
            case CANCELADA:
            case NO_ASISTIO:
                throw new DomainException("No se puede modificar una cita en estado terminal: " + current);

            default:
                throw new DomainException("Estado de cita desconocido: " + current);
        }

        ap.setStatus(newStatus);
        ap = appointments.save(ap);
        return ap;
    }

    /**
     * Obtiene una cita por su ID.
     * 
     * @param id ID de la cita.
     * @return La cita encontrada.
     * @throws ResourceNotFoundException si no existe.
     */
    @Transactional(readOnly = true)
    public Appointment get(Long id) {
        return appointments.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada."));
    }

    /**
     * Lista todas las citas de un paciente, ordenadas por fecha descendente.
     * 
     * @param id ID del paciente.
     * @return Lista de citas del paciente.
     */
    @Transactional(readOnly = true)
    public List<Appointment> byPatient(Long id) {
        return appointments.findByPatientIdOrderByStartTimeDesc(id);
    }

    /**
     * Lista todas las citas de un profesional, ordenadas por fecha ascendente.
     * 
     * @param id ID del profesional.
     * @return Lista de citas del profesional.
     */
    @Transactional(readOnly = true)
    public List<Appointment> byProfessional(Long id) {
        return appointments.findByProfessionalIdOrderByStartTimeAsc(id);
    }
}