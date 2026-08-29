package com.telemed.application.auth;

import com.telemed.infrastructure.persistence.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

/**
 * Filtro que intercepta cada petición HTTP para autenticar mediante JWT.
 * 
 * Se ejecuta una vez por petición (OncePerRequestFilter).
 * Busca el header "Authorization: Bearer <token>", valida el token,
 * y si es válido, establece la autenticación en el contexto de Spring Security.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final UserRepository users;

    public JwtAuthenticationFilter(JwtService jwt, UserRepository users) {
        this.jwt = jwt;
        this.users = users;
    }

    /**
     * Procesa cada solicitud buscando un token JWT en el header.
     * 
     * @param request  Petición HTTP.
     * @param response Respuesta HTTP.
     * @param chain   Cadena de filtros.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String auth = request.getHeader("Authorization");

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            if (jwt.isValid(token)) {
                try {
                    String email = jwt.extractUsername(token);
                    users.findByEmailIgnoreCase(email).ifPresent(user -> {
                        var authorities = List.of(
                                new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())
                        );
                        SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities)
                        );
                    });
                } catch (Exception ignored) {
                    // Si falla la extracción, no se autentica (se ignora)
                }
            }
        }

        chain.doFilter(request, response);
    }
}