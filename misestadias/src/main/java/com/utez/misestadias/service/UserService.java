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

    // ── Convierte User → DTO incluyendo el rol ──
    private StudentProfileDTO toDTO(User user) {
        return profileRepository.findByUser_UserId(user.getUserId())
                .map(profile -> StudentProfileDTO.builder()
                        .profileId(profile.getProfileId())
                        .userId(user.getUserId())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .fullName(profile.getFullName())
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

    // ── Listar solo alumnos activos ──
    @Transactional(readOnly = true)
    public List<StudentProfileDTO> getAllStudents() {
        return userRepository.findAll().stream()
                .filter(u -> "STUDENT".equals(u.getRole()) && u.getIsActive() == 1)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Listar todos los usuarios activos ──
    @Transactional(readOnly = true)
    public List<StudentProfileDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getIsActive() == 1)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Crear nuevo usuario (Admin/Asesor) ──
    @Transactional
    public StudentProfileDTO createUser(CreateUserDTO dto) {

        // Verificar que el email no exista
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ya existe un usuario con el email: " + dto.getEmail()
            );
        }

        // Crear usuario
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setIsActive(1);
        User saved = userRepository.save(user);

        // Si viene fullName, crear perfil básico automáticamente
        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            StudentProfile profile = new StudentProfile();
            profile.setUser(saved);
            profile.setFullName(dto.getFullName());
            profileRepository.save(profile);
        }

        return toDTO(saved);
    }

    // ── Desactivar usuario (soft delete) ──
    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + userId
                ));
        user.setIsActive(0);
        userRepository.save(user);
    }
}
