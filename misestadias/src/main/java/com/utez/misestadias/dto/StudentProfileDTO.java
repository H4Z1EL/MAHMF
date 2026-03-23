package com.utez.misestadias.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfileDTO {

    // Solo lectura — no se envían en el PUT
    private Long profileId;
    private Long userId;
    private String email;

    // Información Personal
    private String fullName;
    private Integer age;
    private String phone;

    // Contacto de Emergencia
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;

    // Información Escolar
    private String major;
    private String division;
    private String matricula;
    private String term;

    // Información de Estadías
    private String companyName;
    private String advisorName;
}