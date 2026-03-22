package com.utez.misestadias.controller;

import com.utez.misestadias.dto.ActivityRequestDTO;
import com.utez.misestadias.dto.ActivityResponseDTO;
import com.utez.misestadias.dto.StatusUpdateDTO;
import com.utez.misestadias.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de actividades.
 * Se guarda en: src/main/java/com/utez/misestadias/controller/ActivityController.java
 *
 * Todos los endpoints requieren JWT válido (configurado en SecurityConfig).
 * Los roles se controlan con @PreAuthorize.
 */
@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    // POST /api/activities
    // Crea una nueva actividad y la asigna a un alumno.
    // Solo ADVISOR y ADMIN pueden crear actividades para los alumnos.
    //
    // Body JSON esperado:
    // {
    //   "title": "Reporte semanal",
    //   "description": "Describe tus actividades...",
    //   "dueDate": "2026-04-01T23:59:00",
    //   "studentId": 5
    // }
    @PostMapping
    @PreAuthorize("hasAnyRole('ADVISOR', 'ADMIN')")
    public ResponseEntity<ActivityResponseDTO> createActivity(
            @Valid @RequestBody ActivityRequestDTO dto) {

        ActivityResponseDTO response = activityService.createActivity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/activities/student/{id}
    // Lista todas las actividades de un alumno específico.
    // El alumno ve las suyas, el asesor ve las de sus alumnos.
    //
    // Ejemplo: GET /api/activities/student/5
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADVISOR', 'ADMIN')")
    public ResponseEntity<List<ActivityResponseDTO>> getActivitiesByStudent(
            @PathVariable Long studentId) {

        List<ActivityResponseDTO> activities = activityService.getActivitiesByStudent(studentId);
        return ResponseEntity.ok(activities);
    }

    // GET /api/activities/{id}
    // Obtiene el detalle de una actividad específica.
    // Cualquier usuario autenticado puede verla (el servicio valida la lógica).
    @GetMapping("/{activityId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADVISOR', 'ADMIN')")
    public ResponseEntity<ActivityResponseDTO> getActivityById(
            @PathVariable Long activityId) {

        ActivityResponseDTO activity = activityService.getActivityById(activityId);
        return ResponseEntity.ok(activity);
    }

    // PUT /api/activities/{id}/status
    // Cambia el estado de una actividad.
    // STUDENT puede marcarla como DELIVERED.
    // ADVISOR/ADMIN pueden cambiarla a cualquier estado.
    //
    // Body JSON esperado:
    // {
    //   "status": "DELIVERED"
    // }
    @PutMapping("/{activityId}/status")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADVISOR', 'ADMIN')")
    public ResponseEntity<ActivityResponseDTO> updateStatus(
            @PathVariable Long activityId,
            @Valid @RequestBody StatusUpdateDTO dto) {

        ActivityResponseDTO updated = activityService.updateStatus(activityId, dto);
        return ResponseEntity.ok(updated);
    }
}