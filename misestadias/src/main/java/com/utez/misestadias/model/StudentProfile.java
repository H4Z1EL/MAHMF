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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;


    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "phone", length = 20)
    private String phone;


    @Column(name = "emergency_contact_name", length = 150)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "emergency_contact_relation", length = 50)
    private String emergencyContactRelation;


    @Column(name = "major", length = 100)
    private String major;

    @Column(name = "division", length = 50)
    private String division;

    @Column(name = "matricula", unique = true, length = 20)
    private String matricula;

    @Column(name = "term", length = 20)
    private String term;


    @Column(name = "company_name", length = 150)
    private String companyName;

    @Column(name = "advisor_name", length = 150)
    private String advisorName;
}
