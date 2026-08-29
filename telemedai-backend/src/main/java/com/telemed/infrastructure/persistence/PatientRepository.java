package com.telemed.infrastructure.persistence;

import com.telemed.domain.auth.User;
import com.telemed.domain.patient.Patient;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para la entidad Patient (pacientes).
 * 
 * Maneja el perfil de los pacientes (información personal y médica).
 */
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUser(User user);
}