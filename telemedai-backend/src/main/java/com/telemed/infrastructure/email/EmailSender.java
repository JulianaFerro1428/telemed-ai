package com.telemed.infrastructure.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador para el envío de correos electrónicos.
 * 
 * Actualmente es una implementación simulada que solo registra en los logs.
 * Está diseñado para ser reemplazado fácilmente por un proveedor real:
 * - SMTP (JavaMailSender)
 * - SendGrid API
 * - Amazon SES
 * - etc.
 * 
 * El método send() recibe el destinatario, asunto y cuerpo del mensaje.
 * En producción, este adaptador se conectará a un servicio externo real.
 */
@Component
public class EmailSender {
    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    /**
     * Envía un correo electrónico (simulado).
     * 
     * @param to Dirección de correo del destinatario.
     * @param subject Asunto del correo.
     * @param body Cuerpo del mensaje (puede ser texto plano o HTML).
     */
    public void send(String to, String subject, String body) {
        log.info("EMAIL_SIMULADO to={} subject={} body={}", to, subject, body);
    }
}