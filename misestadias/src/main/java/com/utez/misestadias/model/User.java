package com.utez.misestadias.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    // NUNCA expongas este campo en un DTO de respuesta
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    // Valores posibles: 'STUDENT', 'ADVISOR', 'ADMIN'
    @Column(name = "role", length = 20)
    private String role;

    // OTP de 4 dígitos para recuperación de contraseña
    @Column(name = "recovery_code", length = 4)
    private String recoveryCode;

    @Column(name = "recovery_expiration")
    private LocalDateTime recoveryExpiration;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private Integer isActive;

    // --- Relaciones ---

    // Un usuario puede tener UN perfil de estudiante (solo si role = STUDENT)
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private StudentProfile studentProfile;

    // --- Ciclo de vida ---

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = 1;
        }
        if (this.role == null) {
            this.role = "STUDENT";
        }
    }
}
