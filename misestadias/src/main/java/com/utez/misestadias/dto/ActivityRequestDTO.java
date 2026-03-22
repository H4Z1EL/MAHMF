package com.utez.misestadias.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ActivityRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    private String title;

    // La descripción puede ser larga (CLOB en BD), no es obligatoria
    private String description;

    @NotNull(message = "La fecha de entrega es obligatoria")
    private LocalDateTime dueDate;

    // El studentId lo recibe el endpoint para saber a qué alumno asignarle
    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long studentId;
}