package com.utez.misestadias.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración central de Spring Security para la API REST.
 * Sigue el estilo moderno de Spring Security 6+ (sin WebSecurityConfigurerAdapter).
 *
 * Se guarda en: src/main/java/com/utez/misestadias/security/SecurityConfig.java
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // Habilita @PreAuthorize en los Controllers
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    // -------------------------------------------------------------------
    // CADENA DE FILTROS DE SEGURIDAD — El corazón de la configuración
    // -------------------------------------------------------------------
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CORS — permite requests desde el panel React (localhost:5173 en dev)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. CSRF — desactivado porque la API es STATELESS (usa JWT, no cookies)
                .csrf(AbstractHttpConfigurer::disable)

                // 3. AUTORIZACIÓN POR RUTA
                .authorizeHttpRequests(auth -> auth

                        // Rutas públicas — sin token
                        .requestMatchers("/api/auth/**").permitAll()

                        // Rutas exclusivas del ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Rutas para ADVISOR y ADMIN
                        .requestMatchers("/api/advisor/**").hasAnyRole("ADVISOR", "ADMIN")

                        // Rutas para el STUDENT (app móvil)
                        .requestMatchers("/api/student/**").hasRole("STUDENT")

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

                // 4. SESIÓN STATELESS — no se guardan sesiones en el servidor
                //    Cada request debe traer su propio JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 5. PROVEEDOR DE AUTENTICACIÓN — conecta Spring Security con tu BD
                .authenticationProvider(authenticationProvider())

                // 6. Registra el filtro JWT ANTES del filtro de usuario/contraseña
                //    para que el token sea procesado primero
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // -------------------------------------------------------------------
    // AUTHENTICATION PROVIDER
    // Le dice a Spring cómo verificar credenciales:
    // - Carga el usuario con UserDetailsServiceImpl
    // - Compara la contraseña con BCrypt
    // -------------------------------------------------------------------
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // -------------------------------------------------------------------
    // AUTHENTICATION MANAGER
    // Lo necesitarás en AuthService para ejecutar el login
    // -------------------------------------------------------------------
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    // -------------------------------------------------------------------
    // BCRYPT PASSWORD ENCODER
    // Siempre usa este bean para hashear contraseñas.
    // NUNCA guardes contraseñas en texto plano.
    // -------------------------------------------------------------------
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // -------------------------------------------------------------------
    // CORS — Configuración para que el panel React pueda llamar a la API
    // En producción, reemplaza "*" con el dominio real del frontend.
    // -------------------------------------------------------------------
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Orígenes permitidos (ajusta el puerto si tu React corre en otro)
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",   // React + Vite (desarrollo)
                "http://localhost:3000"    // Por si usas otro puerto
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}