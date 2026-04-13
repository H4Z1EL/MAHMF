package com.utez.misestadias.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class StatusUpdateDTO {

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(
            regexp = "PENDING|DELIVERED|LATE",
            message = "Estado inválido. Valores permitidos: PENDING, DELIVERED, LATE"
    )
    private String status;
}