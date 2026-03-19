package com.utez.misestadias.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    // Relación OneToOne con User — la FK vive en ESTA tabla (user_id)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // --- Información Personal ---

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "phone", length = 20)
    private String phone;

    // --- Contacto de Emergencia ---

    @Column(name = "emergency_contact_name", length = 150)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "emergency_contact_relation", length = 50)
    private String emergencyContactRelation;

    // --- Información Escolar ---

    @Column(name = "major", length = 100)      // Carrera
    private String major;

    @Column(name = "division", length = 50)    // Ej. DATID
    private String division;

    @Column(name = "matricula", unique = true, length = 20)
    private String matricula;

    @Column(name = "term", length = 20)        // Cuatrimestre
    private String term;

    // --- Información de Estadías ---

    @Column(name = "company_name", length = 150)
    private String companyName;

    @Column(name = "advisor_name", length = 150)
    private String advisorName;
}
