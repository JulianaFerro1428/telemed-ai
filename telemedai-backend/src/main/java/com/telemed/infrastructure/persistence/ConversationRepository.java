package com.telemed.infrastructure.persistence;

import com.telemed.domain.agent.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA para la entidad Conversation (conversaciones del agente).
 * 
 * Persiste las conversaciones entre el paciente y el agente inteligente.
 * Se usa en la preconsulta (aunque actualmente el agente es simulado).
 */
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    /**
     * Lista todas las conversaciones de un paciente, ordenadas por fecha descendente.
     * 
     * @param patientId ID del paciente.
     * @return Lista de conversaciones.
     */
    List<Conversation> findByPatientIdOrderByStartDateDesc(Long patientId);
}