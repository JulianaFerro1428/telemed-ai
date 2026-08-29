package com.telemed.infrastructure.persistence;

import com.telemed.domain.summary.PreconsultationSummary;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para la entidad PreconsultationSummary (resumen de preconsulta).
 * 
 * Persiste los resúmenes generados por el agente inteligente.
 */
public interface PreconsultationSummaryRepository extends JpaRepository<PreconsultationSummary, Long> {
    // Métodos heredados: save(), findById(), findAll(), delete(), etc.
}