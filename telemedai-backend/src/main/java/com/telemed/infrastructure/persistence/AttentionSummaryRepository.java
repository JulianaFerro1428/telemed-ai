package com.telemed.infrastructure.persistence;

import com.telemed.domain.summary.AttentionSummary;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para la entidad AttentionSummary (resumen de atención).
 * 
 * Maneja la persistencia de los resúmenes clínicos generados por el profesional.
 */
public interface AttentionSummaryRepository extends JpaRepository<AttentionSummary, Long> {
    // Métodos heredados: save(), findById(), findAll(), delete(), etc.
}