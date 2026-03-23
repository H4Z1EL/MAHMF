package com.utez.misestadias.service;

import com.utez.misestadias.dto.ActivityRequestDTO;
import com.utez.misestadias.dto.ActivityResponseDTO;
import com.utez.misestadias.dto.StatusUpdateDTO;
import com.utez.misestadias.model.Activity;
import com.utez.misestadias.model.StudentProfile;
import com.utez.misestadias.model.User;
import com.utez.misestadias.repository.ActivityRepository;
import com.utez.misestadias.repository.StudentProfileRepository;
import com.utez.misestadias.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;

    // CREAR ACTIVIDAD
    // El asesor/admin crea la actividad y la asigna a un studentId.
    @Transactional
    public ActivityResponseDTO createActivity(ActivityRequestDTO dto) {

        // 1. Verificar que el estudiante existe
        User student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Estudiante no encontrado con ID: " + dto.getStudentId()
                ));

        // 2. Verificar que el usuario es realmente un STUDENT
        if (!"STUDENT".equals(student.getRole())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El usuario con ID " + dto.getStudentId() + " no es un estudiante"
            );
        }

        // 3. Construir la entidad Activity
        Activity activity = new Activity();
        activity.setStudent(student);
        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setDueDate(dto.getDueDate());
        // El status y createdAt los pone @PrePersist automáticamente

        // 4. Guardar en BD
        Activity saved = activityRepository.save(activity);

        // 5. Convertir a DTO de respuesta
        return toResponseDTO(saved);
    }

    // LISTAR ACTIVIDADES DE UN ALUMNO
    // Lo usa el alumno (ver sus propias) y el asesor (ver las de su alumno).
    @Transactional(readOnly = true)
    public List<ActivityResponseDTO> getActivitiesByStudent(Long studentId) {

        // Verificar que el estudiante existe
        if (!userRepository.existsById(studentId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Estudiante no encontrado con ID: " + studentId
            );
        }

        return activityRepository
                .findByStudent_UserIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ACTUALIZAR ESTADO DE UNA ACTIVIDAD
    // Puede hacerlo el asesor (DELIVERED, LATE) o el alumno (DELIVERED).
    @Transactional
    public ActivityResponseDTO updateStatus(Long activityId, StatusUpdateDTO dto) {

        // 1. Buscar la actividad
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Actividad no encontrada con ID: " + activityId
                ));

        // 2. Validar transiciones de estado permitidas
        validateStatusTransition(activity.getStatus(), dto.getStatus());

        // 3. Actualizar estado — @PreUpdate se encarga del updatedAt
        activity.setStatus(dto.getStatus());

        Activity updated = activityRepository.save(activity);
        return toResponseDTO(updated);
    }

    // OBTENER UNA ACTIVIDAD POR ID
    @Transactional(readOnly = true)
    public ActivityResponseDTO getActivityById(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Actividad no encontrada con ID: " + activityId
                ));
        return toResponseDTO(activity);
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {

        // Si el estado es el mismo, no hay nada que cambiar
        if (currentStatus.equals(newStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La actividad ya tiene el estado: " + currentStatus
            );
        }

    }

    private ActivityResponseDTO toResponseDTO(Activity activity) {

        // Intentar obtener el nombre del alumno desde su perfil
        String studentName = studentProfileRepository
                .findByUser_UserId(activity.getStudent().getUserId())
                .map(StudentProfile::getFullName)
                .orElse("Sin perfil");

        return ActivityResponseDTO.builder()
                .activityId(activity.getActivityId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .dueDate(activity.getDueDate())
                .status(activity.getStatus())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .studentId(activity.getStudent().getUserId())
                .studentName(studentName)
                .studentEmail(activity.getStudent().getEmail())
                .build();
    }
}