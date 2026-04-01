package com.utez.misestadias.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. VÍA RÁPIDA: Si la ruta es de autenticación, no pedimos Token.
        if (request.getServletPath().contains("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Leer el header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 3. Si no hay token o no es Bearer, seguimos sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. Extraer token y email
        jwt = authHeader.substring(7);
        userEmail = jwtUtils.extractUsername(jwt);

        // 5. Si hay email y no está autenticado aún
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Validar token
            if (jwtUtils.validateToken(jwt)) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 7. Crear la autenticación oficial de Spring
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 8. Meter al usuario al "Club" de Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 9. Continuar
        filterChain.doFilter(request, response);
    }
}