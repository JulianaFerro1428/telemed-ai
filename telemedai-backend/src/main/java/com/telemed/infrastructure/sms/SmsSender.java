package com.telemed.infrastructure.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Adaptador para el envío de SMS.
 * 
 * Actualmente es una implementación simulada que solo registra en los logs.
 * Está diseñado para ser reemplazado por un proveedor real:
 * - Twilio API
 * - Amazon SNS
 * - Nexmo/Vonage
 * - etc.
 * 
 * El método send() recibe el número de teléfono y el mensaje.
 * En producción, este adaptador se conectará a un servicio externo real.
 */
@Component
public class SmsSender {
    private static final Logger log = LoggerFactory.getLogger(SmsSender.class);

    /**
     * Envía un SMS (simulado).
     * 
     * @param phone Número de teléfono del destinatario (con código de país).
     * @param message Mensaje a enviar (limitado a 160 caracteres para SMS estándar).
     */
    public void send(String phone, String message) {
        log.info("SMS_SIMULADO phone={} message={}", phone, message);
    }
}