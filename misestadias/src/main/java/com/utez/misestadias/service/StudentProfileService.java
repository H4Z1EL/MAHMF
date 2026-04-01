package com.utez.misestadias.service;

import com.utez.misestadias.dto.StudentProfileDTO;
import com.utez.misestadias.model.StudentProfile;
import com.utez.misestadias.model.User;
import com.utez.misestadias.repository.StudentProfileRepository;
import com.utez.misestadias.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentProfileService {

    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public StudentProfileDTO getMyProfile(String email) {
        log.info("Buscando perfil para el email: {}", email);

        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return profileRepository.findByUser_UserId(user.getUserId())
                .map(profile -> toDTO(profile, user))
                // Si no tiene perfil, regresamos uno vacío con sus datos base para que Android no truene
                .orElse(StudentProfileDTO.builder()
                        .userId(user.getUserId())
                        .email(user.getEmail())
                        .fullName("Usuario nuevo")
                        .build());
    }

    @Transactional
    public StudentProfileDTO upsertProfile(String email, StudentProfileDTO dto) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Buscamos el perfil existente o creamos uno nuevo
        StudentProfile profile = profileRepository.findByUser_UserId(user.getUserId())
                .orElse(new StudentProfile());

        profile.setUser(user);

        // Actualización selectiva de campos
        if (dto.getFullName() != null)                 profile.setFullName(dto.getFullName());
        if (dto.getAge() != null)                      profile.setAge(dto.getAge());
        if (dto.getPhone() != null)                    profile.setPhone(dto.getPhone());
        if (dto.getEmergencyContactName() != null)     profile.setEmergencyContactName(dto.getEmergencyContactName());
        if (dto.getEmergencyContactPhone() != null)    profile.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        if (dto.getEmergencyContactRelation() != null) profile.setEmergencyContactRelation(dto.getEmergencyContactRelation());
        if (dto.getMajor() != null)                    profile.setMajor(dto.getMajor());
        if (dto.getDivision() != null)                 profile.setDivision(dto.getDivision());
        if (dto.getMatricula() != null)                profile.setMatricula(dto.getMatricula());
        if (dto.getTerm() != null)                     profile.setTerm(dto.getTerm());
        if (dto.getCompanyName() != null)              profile.setCompanyName(dto.getCompanyName());
        if (dto.getAdvisorName() != null)              profile.setAdvisorName(dto.getAdvisorName());

        StudentProfile saved = profileRepository.save(profile);
        return toDTO(saved, user);
    }

    @Transactional(readOnly = true)
    public StudentProfileDTO getProfileByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return profileRepository.findByUser_UserId(userId)
                .map(profile -> toDTO(profile, user))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil no encontrado"));
    }

    private StudentProfileDTO toDTO(StudentProfile profile, User user) {
        return StudentProfileDTO.builder()
                .profileId(profile.getProfileId())
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(profile.getFullName())
                .age(profile.getAge())
                .phone(profile.getPhone())
                .emergencyContactName(profile.getEmergencyContactName())
                .emergencyContactPhone(profile.getEmergencyContactPhone())
                .emergencyContactRelation(profile.getEmergencyContactRelation())
                .major(profile.getMajor())
                .division(profile.getDivision())
                .matricula(profile.getMatricula())
                .term(profile.getTerm())
                .companyName(profile.getCompanyName())
                .advisorName(profile.getAdvisorName())
                .build();
    }
}