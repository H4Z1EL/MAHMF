package com.utez.misestadias.service;

import com.utez.misestadias.dto.CreateUserDTO;
import com.utez.misestadias.dto.StudentProfileDTO;
import com.utez.misestadias.model.StudentProfile;
import com.utez.misestadias.model.User;
import com.utez.misestadias.repository.StudentProfileRepository;
import com.utez.misestadias.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StudentProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    private StudentProfileDTO toDTO(User user) {
        return profileRepository.findByUser_UserId(user.getUserId())
                .map(profile -> StudentProfileDTO.builder()
                        .profileId(profile.getProfileId())
                        .userId(user.getUserId())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .fullName(profile.getFullName())
                        .age(profile.getAge())
                        .phone(profile.getPhone())
                        .major(profile.getMajor())
                        .division(profile.getDivision())
                        .matricula(profile.getMatricula())
                        .term(profile.getTerm())
                        .companyName(profile.getCompanyName())
                        .advisorName(profile.getAdvisorName())
                        .build())
                .orElse(StudentProfileDTO.builder()
                        .userId(user.getUserId())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .fullName("Sin perfil")
                        .build());
    }

    @Transactional(readOnly = true)
    public List<StudentProfileDTO> getAllStudents() {
        return userRepository.findAll().stream()
                .filter(u -> "STUDENT".equals(u.getRole()) && u.getIsActive() == 1)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StudentProfileDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getIsActive() == 1)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public StudentProfileDTO createUser(CreateUserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un usuario con el email: " + dto.getEmail());
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setIsActive(1);
        User saved = userRepository.save(user);

        StudentProfile profile = new StudentProfile();
        profile.setUser(saved);
        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            profile.setFullName(dto.getFullName());
        }
        if (dto.getAdvisorName() != null && !dto.getAdvisorName().isBlank()) {
            profile.setAdvisorName(dto.getAdvisorName());
        }
        profileRepository.save(profile);

        return toDTO(saved);
    }

    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado: " + userId));
        user.setIsActive(0);
        userRepository.save(user);
    }

    @Transactional
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado: " + userId));
        user.setIsActive(1);
        userRepository.save(user);
    }

    @Transactional
    public StudentProfileDTO updateProfileByUserId(Long userId, StudentProfileDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuario no encontrado: " + userId));

        StudentProfile profile = profileRepository.findByUser_UserId(userId)
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
        profile.setAdvisorName(dto.getAdvisorName());

        profileRepository.save(profile);
        return toDTO(user);
    }
}
