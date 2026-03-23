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
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public AuthResponse login(AuthRequest request) {
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales inválidas"
            );
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado"
                ));

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole())
                .userId(user.getUserId())
                .email(user.getEmail())
                .build();
    }
}