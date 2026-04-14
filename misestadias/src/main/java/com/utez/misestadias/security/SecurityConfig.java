package com.utez.misestadias.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/files/**").permitAll()

                        .requestMatchers(HttpMethod.GET,  "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/api/users/students").hasAnyRole("ADMIN", "ADVISOR")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/activate").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/profile").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/profiles/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/profiles").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/profiles/**").hasAnyRole("ADMIN", "ADVISOR")

                        .requestMatchers(HttpMethod.GET, "/api/activities/summary").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.GET, "/api/activities").hasRole("STUDENT")

                        .requestMatchers(HttpMethod.POST, "/api/activities").hasAnyRole("ADMIN", "ADVISOR")

                        .requestMatchers(HttpMethod.GET, "/api/activities/student/**").hasAnyRole("ADMIN", "ADVISOR")

                        .requestMatchers(HttpMethod.GET, "/api/activities/**").authenticated()

                        .requestMatchers(HttpMethod.PUT, "/api/activities/*/status").hasAnyRole("ADMIN", "ADVISOR", "STUDENT")

                        .requestMatchers(HttpMethod.POST, "/api/activities/*/upload").hasRole("STUDENT")

                        .requestMatchers(HttpMethod.GET, "/api/activities/*/attachments").authenticated()

                        .requestMatchers("/api/activities/*/comments/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/activities/*/comments").authenticated()

                        .anyRequest().denyAll()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
