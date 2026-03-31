package com.utez.misestadias.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUserDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "STUDENT|ADVISOR|ADMIN", message = "Rol inválido")
    private String role;

    // Opcional — nombre completo para crear el perfil al mismo tiempo
    private String fullName;
}
