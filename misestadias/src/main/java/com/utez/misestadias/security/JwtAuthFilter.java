package com.utez.misestadias.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que se ejecuta UNA VEZ por cada request HTTP.
 * Su trabajo: leer el header "Authorization", validar el JWT
 * y cargar al usuario en el contexto de seguridad de Spring.
 *
 * Se guarda en: src/main/java/com/utez/misestadias/security/JwtAuthFilter.java
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Lee el header "Authorization" del request
        //    La app móvil y el panel web deben enviarlo así:
        //    Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
        final String authHeader = request.getHeader("Authorization");

        // 2. Si no hay header o no empieza con "Bearer ", deja pasar
        //    (SecurityConfig decidirá si el endpoint requiere auth o no)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extrae el token quitando el prefijo "Bearer "
        final String jwt = authHeader.substring(7);

        // 4. Extrae el email del token
        final String email = jwtUtils.extractUsername(jwt);

        // 5. Si hay email y NO hay una autenticación previa en el contexto
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Valida el token
            if (jwtUtils.validateToken(jwt)) {

                // 7. Carga el usuario completo desde la BD
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // 8. Crea el objeto de autenticación de Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities() // ROLE_STUDENT / ROLE_ADVISOR / ROLE_ADMIN
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 9. Registra la autenticación en el contexto — a partir de aquí
                //    Spring Security sabe quién es el usuario en este request
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 10. Continúa con el siguiente filtro de la cadena
        filterChain.doFilter(request, response);
    }
}