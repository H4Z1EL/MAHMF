package com.utez.misestadias.controller;

import com.utez.misestadias.dto.ActivityRequestDTO;
import com.utez.misestadias.dto.ActivityResponseDTO;
import com.utez.misestadias.dto.StatusUpdateDTO;
import com.utez.misestadias.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    // 1. Resumen filtrado por el usuario del Token
    @GetMapping("/summary")
    public ResponseEntity<?> getActivitiesSummary(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(activityService.getSummary(userDetails.getUsername()));
    }

    // 2. Listado filtrado por el usuario del Token (con o sin status)
    @GetMapping
    public ResponseEntity<List<ActivityResponseDTO>> getActivities(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String status) {

        if (status != null) {
            return ResponseEntity.ok(activityService.getActivitiesByStatus(userDetails.getUsername(), status));
        }
        return ResponseEntity.ok(activityService.getActivitiesByStudentEmail(userDetails.getUsername()));
    }

    // 3. Este lo dejamos por si el Asesor necesita ver las de un alumno específico
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ActivityResponseDTO>> getActivitiesByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(activityService.getActivitiesByStudent(studentId));
    }

    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponseDTO> getActivityById(@PathVariable Long activityId) {
        return ResponseEntity.ok(activityService.getActivityById(activityId));
    }

    @PostMapping
    public ResponseEntity<ActivityResponseDTO> createActivity(
            @Valid @RequestBody ActivityRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(activityService.createActivity(dto));
    }

    @PutMapping("/{activityId}/status")
    public ResponseEntity<ActivityResponseDTO> updateStatus(
            @PathVariable Long activityId,
            @Valid @RequestBody StatusUpdateDTO dto) {
        return ResponseEntity.ok(activityService.updateStatus(activityId, dto));
    }
}