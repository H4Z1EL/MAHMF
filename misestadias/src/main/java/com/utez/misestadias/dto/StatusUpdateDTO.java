package com.utez.misestadias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar SOLO el estado de una actividad.
 * PUT /api/activities/{id}/status
 *
 * Se guarda en: src/main/java/com/utez/misestadias/dto/StatusUpdateDTO.java
 */
@Data
@NoArgsConstructor
public class StatusUpdateDTO {

    // Solo acepta los tres valores válidos definidos en el DDL
    @NotBlank(message = "El estado es obligatorio")
    @Pattern(
            regexp = "PENDING|DELIVERED|LATE",
            message = "Estado inválido. Valores permitidos: PENDING, DELIVERED, LATE"
    )
    private String status;
}