package com.telemed.infrastructure.persistence;

import com.telemed.domain.summary.PostSummary;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para la entidad PostSummary (resumen posterior).
 * 
 * Persiste los resúmenes que combinan preconsulta y atención.
 */
public interface PostSummaryRepository extends JpaRepository<PostSummary, Long> {
    // Métodos heredados: save(), findById(), findAll(), delete(), etc.
}