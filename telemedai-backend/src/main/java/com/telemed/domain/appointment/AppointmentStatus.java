package com.telemed.domain.appointment;

/**
 * Estados del ciclo de vida de una cita.
 * 
 * Transiciones permitidas:
 * - CONFIRMADA → REPROGRAMADA, CANCELADA, COMPLETADA, NO_ASISTIO
 * - REPROGRAMADA → CONFIRMADA, CANCELADA
 * - COMPLETADA, CANCELADA, NO_ASISTIO son terminales (no se pueden modificar).
 */
public enum AppointmentStatus {
    CONFIRMADA,
    REPROGRAMADA,
    COMPLETADA,
    CANCELADA,
    NO_ASISTIO
}