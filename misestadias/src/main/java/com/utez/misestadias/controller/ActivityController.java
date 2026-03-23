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

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADVISOR', 'ROLE_ADMIN')")
    public ResponseEntity<ActivityResponseDTO> createActivity(
            @Valid @RequestBody ActivityRequestDTO dto) {

        ActivityResponseDTO response = activityService.createActivity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_ADVISOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<ActivityResponseDTO>> getActivitiesByStudent(
            @PathVariable Long studentId) {

        List<ActivityResponseDTO> activities = activityService.getActivitiesByStudent(studentId);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/{activityId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_ADVISOR', 'ROLE_ADMIN')")
    public ResponseEntity<ActivityResponseDTO> getActivityById(
            @PathVariable Long activityId) {

        ActivityResponseDTO activity = activityService.getActivityById(activityId);
        return ResponseEntity.ok(activity);
    }

    @PutMapping("/{activityId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT', 'ROLE_ADVISOR', 'ROLE_ADMIN')")
    public ResponseEntity<ActivityResponseDTO> updateStatus(
            @PathVariable Long activityId,
            @Valid @RequestBody StatusUpdateDTO dto) {

        ActivityResponseDTO updated = activityService.updateStatus(activityId, dto);
        return ResponseEntity.ok(updated);
    }
}