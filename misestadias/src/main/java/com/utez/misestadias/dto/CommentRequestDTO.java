package com.utez.misestadias.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CommentRequestDTO {

    @NotBlank(message = "El contenido del comentario es obligatorio")
    private String content;
}