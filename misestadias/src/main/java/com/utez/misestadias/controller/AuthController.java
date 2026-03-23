package com.utez.misestadias.controller;

import com.utez.misestadias.dto.AuthRequest;
import com.utez.misestadias.dto.AuthResponse;
import com.utez.misestadias.dto.RecoverPasswordRequest;
import com.utez.misestadias.dto.ResetPasswordRequest;
import com.utez.misestadias.dto.VerifyCodeRequest;
import com.utez.misestadias.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/recover-password")
    public ResponseEntity<Map<String, String>> recoverPassword(
            @Valid @RequestBody RecoverPasswordRequest request) {
        String message = authService.requestPasswordRecovery(request.getEmail());
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Boolean>> verifyCode(
            @Valid @RequestBody VerifyCodeRequest request) {
        boolean valid = authService.verifyCode(request.getEmail(), request.getCode());
        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        String message = authService.resetPassword(
                request.getEmail(),
                request.getCode(),
                request.getNewPassword()
        );
        return ResponseEntity.ok(Map.of("message", message));
    }
}