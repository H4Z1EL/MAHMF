package com.utez.misestadias.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;       // El JWT que el cliente debe guardar
    private String role;        // STUDENT | ADVISOR | ADMIN
    private Long userId;        // ID del usuario autenticado
    private String email;       // Email confirmado
}