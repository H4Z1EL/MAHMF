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
    private String description;

    @NotNull(message = "La fecha de entrega es obligatoria")
    private LocalDateTime dueDate;

    @NotNull(message = "El ID del estudiante es obligatorio")
    private Long studentId;
}