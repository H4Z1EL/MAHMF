package com.utez.misestadias.service;

import com.utez.misestadias.dto.AuthRequest;
import com.utez.misestadias.dto.AuthResponse;
import com.utez.misestadias.model.User;
import com.utez.misestadias.repository.UserRepository;
import com.utez.misestadias.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;


    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole())
                .userId(user.getUserId())
                .email(user.getEmail())
                .build();
    }

    @Transactional
    public String requestPasswordRecovery(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {

            String code = String.format("%04d", new SecureRandom().nextInt(9000) + 1000);

            user.setRecoveryCode(code);
            user.setRecoveryExpiration(LocalDateTime.now().plusMinutes(10));
            userRepository.save(user);

            emailService.sendRecoveryCode(email, code);
        });

        return "Si el email existe, recibirás un código de recuperación.";
    }

    @Transactional(readOnly = true)
    public boolean verifyCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Verificar que tiene código y no expiró
        if (user.getRecoveryCode() == null || user.getRecoveryExpiration() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay código de recuperación activo");
        }

        if (LocalDateTime.now().isAfter(user.getRecoveryExpiration())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El código ha expirado. Solicita uno nuevo.");
        }

        if (!user.getRecoveryCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código incorrecto");
        }

        return true;
    }

    @Transactional
    public String resetPassword(String email, String code, String newPassword) {

        verifyCode(email, code);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));

        user.setRecoveryCode(null);
        user.setRecoveryExpiration(null);

        userRepository.save(user);

        return "Contraseña actualizada exitosamente.";
    }
}