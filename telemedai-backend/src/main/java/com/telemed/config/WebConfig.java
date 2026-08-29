package com.telemed.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuración general de la aplicación web.
 * 
 * Actualmente, habilita tareas programadas usando @Scheduled en los servicios.
 * 
 * Esta configuración puede extenderse en el futuro para incluir:
 * - Configuración de interceptores
 * - Conversores de formato
 * - Configuración de recursos estáticos
 */
@Configuration
@EnableScheduling
public class WebConfig {
    // Esta clase solo habilita las tareas programadas.
    // Los métodos @Scheduled se definen en los servicios correspondientes.
}