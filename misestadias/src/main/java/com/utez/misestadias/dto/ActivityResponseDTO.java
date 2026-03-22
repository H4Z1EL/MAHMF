package com.utez.misestadias.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityResponseDTO {

    private Long activityId;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private String status;           // PENDING | DELIVERED | LATE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Datos básicos del alumno dueño de esta actividad
    private Long studentId;
    private String studentName;      // Viene de StudentProfile.fullName
    private String studentEmail;     // Viene de User.email
}