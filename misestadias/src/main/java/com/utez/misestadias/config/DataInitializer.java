package com.utez.misestadias.config;

import com.utez.misestadias.model.User;
import com.utez.misestadias.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (!userRepository.existsByEmail("admin@correo.com")) {

            User admin = new User();
            admin.setEmail("admin@correo.com");
            // BCrypt encripta "12345" → algo como "$2a$10$..."
            admin.setPasswordHash(passwordEncoder.encode("12345"));
            admin.setRole("ADMIN");
            admin.setIsActive(1);

            userRepository.save(admin);

            log.info("========================================");
            log.info("  Usuario ADMIN creado exitosamente");
            log.info("  Email:    admin@correo.com");
            log.info("  Password: 12345");
            log.info("========================================");

        } else {
            log.info("Usuario ADMIN ya existe — no se insertó de nuevo.");
        }

        if (!userRepository.existsByEmail("alumno@correo.com")) {
            User alumno = new User();
            alumno.setEmail("alumno@correo.com");
            alumno.setPasswordHash(passwordEncoder.encode("12345"));
            alumno.setRole("STUDENT");
            alumno.setIsActive(1);
            userRepository.save(alumno);
            log.info("Usuario STUDENT creado: alumno@correo.com / 12345");
        }

        if (!userRepository.existsByEmail("asesor@correo.com")) {
            User asesor = new User();
            asesor.setEmail("asesor@correo.com");
            asesor.setPasswordHash(passwordEncoder.encode("12345"));
            asesor.setRole("ADVISOR");
            asesor.setIsActive(1);
            userRepository.save(asesor);
            log.info("Usuario ADVISOR creado: asesor@correo.com / 12345");
        }

    }
}