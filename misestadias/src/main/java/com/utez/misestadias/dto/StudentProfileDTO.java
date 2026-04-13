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

    private Long profileId;
    private Long userId;
    private String email;
    private String role;

    private String fullName;
    private Integer age;
    private String phone;

    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelation;

    private String major;
    private String division;
    private String matricula;
    private String term;

    private String companyName;
    private String advisorName;
}
