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
public class CommentResponseDTO {

    private Long commentId;
    private String content;
    private LocalDateTime createdAt;

    // Datos del autor (alumno o asesor)
    private Long authorId;
    private String authorEmail;
    private String authorRole;

    // Referencia a la actividad
    private Long activityId;
}