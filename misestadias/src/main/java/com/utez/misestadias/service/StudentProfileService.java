package com.utez.misestadias.service;

import com.utez.misestadias.dto.StudentProfileDTO;
import com.utez.misestadias.model.StudentProfile;
import com.utez.misestadias.model.User;
import com.utez.misestadias.repository.StudentProfileRepository;
import com.utez.misestadias.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public StudentProfileDTO getMyProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return profileRepository.findByUser_UserId(user.getUserId())
                .map(profile -> toDTO(profile, user))
                .orElse(StudentProfileDTO.builder().userId(user.getUserId()).email(user.getEmail()).build());
    }

    @Transactional
    public StudentProfileDTO upsertProfile(String email, StudentProfileDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        StudentProfile profile = profileRepository.findByUser_UserId(user.getUserId())
                .orElse(new StudentProfile());
        profile.setUser(user);

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + userId));
        return profileRepository.findByUser_UserId(userId)
                .map(profile -> toDTO(profile, user))
                .orElse(StudentProfileDTO.builder().userId(user.getUserId()).email(user.getEmail()).build());
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