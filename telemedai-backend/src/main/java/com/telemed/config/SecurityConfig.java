package com.telemed.config;

import com.telemed.application.auth.JwtAuthenticationFilter;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

/**
 * Configuración de seguridad de la aplicación.
 * 
 * Define:
 * - Políticas de autenticación: stateless (sin sesiones), usando JWT.
 * - Autorización: roles (PACIENTE, PROFESIONAL, ADMIN) mediante @PreAuthorize.
 * - CORS: permite solicitudes desde cualquier origen (configurable para producción).
 * - Filtro JWT: se ejecuta antes de la autenticación estándar de Spring.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /**
     * Define la cadena de filtros de seguridad.
     * 
     * @param http Objeto HttpSecurity para configurar la seguridad.
     * @return SecurityFilterChain configurado.
     * @throws Exception Si ocurre un error en la configuración.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // Deshabilitar CSRF por ser stateless y usar JWT
            .csrf(csrf -> csrf.disable())
            
            // Configurar CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Sin sesiones (stateless)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Reglas de autorización
            .authorizeHttpRequests(a -> a
                // Endpoints públicos
                .requestMatchers(
                    "/api/auth/**",           // Registro, login, refresh, recuperación
                    "/swagger-ui/**",         // Documentación OpenAPI
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/actuator/health"        // Health check
                ).permitAll()
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            
            // Agregar el filtro JWT antes del filtro de autenticación por username/password
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    /**
     * Bean para el codificador de contraseñas.
     * 
     * Usa BCrypt con factor de costo 10 (por defecto).
     * Es el mismo encoder que se usa al registrar usuarios y al validar credenciales.
     * 
     * @return PasswordEncoder de BCrypt.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración de CORS (Cross-Origin Resource Sharing).
     * 
     * Permite solicitudes desde cualquier origen (*) en desarrollo.
     * En producción, se debe restringir a orígenes específicos.
     * 
     * @return CorsConfigurationSource con la configuración.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("*"));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        c.setAllowedHeaders(List.of("*"));
        return request -> c;
    }
}