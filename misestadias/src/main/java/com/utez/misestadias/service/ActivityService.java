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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;

    @Transactional(readOnly = true)
    public Map<String, Integer> getSummary(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        List<Activity> myActivities = activityRepository.findByStudent_UserIdOrderByCreatedAtDesc(user.getUserId());

        int total = myActivities.size();
        int completed = (int) myActivities.stream()
                .filter(a -> "DELIVERED".equals(a.getStatus()))
                .count();

        return Map.of("completed", completed, "total", total);
    }

    @Transactional(readOnly = true)
    public List<ActivityResponseDTO> getActivitiesByStudentEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return activityRepository.findByStudent_UserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityResponseDTO> getActivitiesByStatus(String email, String status) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return activityRepository.findByStudent_UserIdAndStatusOrderByCreatedAtDesc(user.getUserId(), status)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ActivityResponseDTO createActivity(ActivityRequestDTO dto) {
        User student = userRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));

        Activity activity = new Activity();
        activity.setStudent(student);
        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setDueDate(dto.getDueDate());
        activity.setStatus("PENDING");

        return toResponseDTO(activityRepository.save(activity));
    }

    @Transactional(readOnly = true)
    public List<ActivityResponseDTO> getActivitiesByStudent(Long studentId) {
        return activityRepository.findByStudent_UserIdOrderByCreatedAtDesc(studentId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ActivityResponseDTO updateStatus(Long activityId, StatusUpdateDTO dto) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actividad no encontrada"));

        activity.setStatus(dto.getStatus());
        return toResponseDTO(activityRepository.save(activity));
    }

    @Transactional(readOnly = true)
    public ActivityResponseDTO getActivityById(Long activityId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Actividad no encontrada"));
        return toResponseDTO(activity);
    }

    private ActivityResponseDTO toResponseDTO(Activity activity) {
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