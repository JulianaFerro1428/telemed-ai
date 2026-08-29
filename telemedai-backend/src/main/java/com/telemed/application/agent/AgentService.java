package com.telemed.application.agent;

import com.telemed.domain.agent.*;
import com.telemed.infrastructure.persistence.*;
import com.telemed.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Servicio que implementa el caso de uso del agente conversacional simulado.
 * 
 * El agente no utiliza IA real; sigue un flujo predefinido de preguntas y respuestas.
 * Su objetivo es recopilar información del paciente para la preconsulta,
 * sin diagnosticar ni recetar medicamentos.
 */
@Service
public class AgentService {
    private final ConversationRepository conversations;
    private final PatientRepository patients;

    public AgentService(ConversationRepository c, PatientRepository p) {
        conversations = c;
        patients = p;
    }

    /**
     * Inicia una nueva conversación con el agente para un paciente.
     * 
     * @param patientId ID del paciente que inicia la conversación.
     * @return La conversación recién creada con el mensaje de bienvenida del agente.
     * @throws ResourceNotFoundException si el paciente no existe.
     */
    @Transactional
    public Conversation start(Long patientId) {
        var p = patients.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado."));
        
        Conversation c = new Conversation(p);
        Message m = new Message(c, Sender.AGENTE,
                "Hola. Soy el asistente de preconsulta. ¿Cuál es el motivo principal de tu consulta?");
        c.addMessage(m);
        return conversations.save(c);
    }

    /**
     * Procesa un mensaje del paciente y genera la respuesta del agente.
     * 
     * @param conversationId ID de la conversación activa.
     * @param content Mensaje enviado por el paciente.
     * @return El mensaje de respuesta del agente.
     * @throws ResourceNotFoundException si la conversación no existe.
     */
    @Transactional
    public Message respond(Long conversationId, String content) {
        Conversation c = get(conversationId);
        
        // Registrar mensaje del paciente
        Message user = new Message(c, Sender.PACIENTE, content);
        c.addMessage(user);
        
        // Generar respuesta según el número de mensajes
        String response = nextQuestion(c.getMessages().size());
        Message agent = new Message(c, Sender.AGENTE, response);
        c.addMessage(agent);
        
        conversations.save(c);
        return agent;
    }

    /**
     * Define la secuencia de preguntas del agente según el número de mensajes.
     * 
     * @param count Número de mensajes en la conversación (incluyendo el mensaje del agente).
     * @return La pregunta correspondiente.
     */
    private String nextQuestion(int count) {
        return switch (count) {
            case 2 -> "¿Desde cuándo presentas estos síntomas y han empeorado, mejorado o permanecido igual?";
            case 4 -> "¿Qué síntomas específicos presentas y con qué intensidad?";
            case 6 -> "¿Tienes antecedentes médicos o medicamentos que debamos registrar para informar al profesional?";
            default -> "Gracias. ¿Hay algún otro dato relevante que quieras agregar? Recuerda que este asistente no realiza diagnósticos ni prescribe medicamentos.";
        };
    }

    /**
     * Finaliza la conversación (la marca como FINALIZADA).
     * 
     * @param id ID de la conversación a finalizar.
     * @return La conversación actualizada.
     * @throws ResourceNotFoundException si la conversación no existe.
     */
    @Transactional
    public Conversation finish(Long id) {
        Conversation c = get(id);
        c.finish();
        return conversations.save(c);
    }

    /**
     * Obtiene una conversación por su ID.
     * 
     * @param id ID de la conversación.
     * @return La conversación encontrada.
     * @throws ResourceNotFoundException si no existe.
     */
    public Conversation get(Long id) {
        return conversations.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversación no encontrada."));
    }

    /**
     * Lista todas las conversaciones de un paciente, ordenadas por fecha descendente.
     * 
     * @param id ID del paciente.
     * @return Lista de conversaciones.
     */
    public List<Conversation> byPatient(Long id) {
        return conversations.findByPatientIdOrderByStartDateDesc(id);
    }
}